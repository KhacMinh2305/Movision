package architecture.data.source.other;
import androidx.annotation.NonNull;
import androidx.annotation.experimental.UseExperimental;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadType;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxRemoteMediator;
import java.util.List;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.RemoteKey;
import architecture.data.model.genre.Genre;
import architecture.data.network.api.TmdbServices;
import architecture.domain.MovieConversionHelper;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@UseExperimental(markerClass = ExperimentalPagingApi.class)
public class CategoryMovieRemoteMediator extends RxRemoteMediator<Integer, Movie> {

    private final LocalDatabase database;
    private final TmdbServices apiService;
    private final MovieDao movieDao;
    private final RemoteKeyDao remoteKeyDao;
    private final List<Genre> movieGenres;
    private String remoteKeyTag;
    private String movieTag;
    private boolean endOfPaginationReached;

    public CategoryMovieRemoteMediator(LocalDatabase database, TmdbServices apiService,
                                       MovieDao movieDao, RemoteKeyDao remoteKeyDao,
                                       List<Genre> movieGenres) {
        this.database = database;
        this.apiService = apiService;
        this.movieDao = movieDao;
        this.remoteKeyDao = remoteKeyDao;
        this.movieGenres = movieGenres;
    }

    public void setMovieInfo(String tag) {
        this.remoteKeyTag = tag;
        this.movieTag = tag;
    }

    @NonNull
    @Override
    public Single<InitializeAction> initializeSingle() {
        return Single.just(InitializeAction.SKIP_INITIAL_REFRESH);
    }

    // test , dung o flatmap cho load movie theo tag
    /*private List<Movie> convertApiDataToLocalData(List<ApiMovieResult> apiResult) {
        List<Movie> listEntity = new ArrayList<>();
        for(ApiMovieResult result : apiResult) {
            listEntity.add(result.toEntity(movieTag));
        }
        return listEntity;
    }*/


    private Single<MediatorResult> handleData(LoadType loadType, List<Movie> listEntities, int nextKey, int totalPages) {
        database.runInTransaction(() -> {
            if(loadType == LoadType.REFRESH) {
                // delete all if refresh
                remoteKeyDao.deleteRemoteKeys(remoteKeyTag);
                movieDao.deleteMovies(movieTag);
            }
            endOfPaginationReached = nextKey == totalPages;
            remoteKeyDao.insertRemoteKey(new RemoteKey(nextKey + 1, remoteKeyTag));
            movieDao.insertMovies(listEntities);
        });
        return Single.just(new MediatorResult.Success(endOfPaginationReached));
    }

    public Single<MediatorResult> loadMoreMovie(Single<RemoteKey> remoteKeySource, @NonNull LoadType loadType) {
        return remoteKeySource
                .subscribeOn(Schedulers.io())
                .flatMap(key -> {
                    // check if key is not valid but still not end of pagination
                    if(loadType != LoadType.REFRESH && key.nextKey == 1) {
                        return Single.just(new MediatorResult.Success(true));
                    }
                    MovieConversionHelper helper = new MovieConversionHelper();
                    int nextKey = key.nextKey;
                    if(AppConstant.CATEGORY_TRENDING_TAG.equals(movieTag)) {
                        return apiService.loadTrendingMovies("day", nextKey) //convertApiDataToLocalData(apiMovie.getResults())
                                .flatMap(apiMovie -> handleData(loadType, helper.convertApiDataToLocalData(apiMovie.getResults(), movieTag, movieGenres),
                                        nextKey, apiMovie.getTotalPages()));
                    } else if(AppConstant.CATEGORY_PLAYING_TAG.equals(movieTag)) {
                        return apiService.loadPlayingMovies(nextKey)
                                .flatMap(apiMovie -> handleData(loadType, helper.convertApiDataToLocalData(apiMovie.getResults(), movieTag, movieGenres),
                                        nextKey, apiMovie.getTotalPages()));
                    } else if(AppConstant.CATEGORY_POPULAR_TAG.equals(movieTag)) {
                        return apiService.loadPopularMovies(nextKey)
                                .flatMap(apiMovie -> handleData(loadType, helper.convertApiDataToLocalData(apiMovie.getResults(), movieTag, movieGenres),
                                        nextKey, apiMovie.getTotalPages()));
                    } else if(AppConstant.CATEGORY_TOP_RATED_TAG.equals(movieTag)) {
                        return apiService.loadTopRatedMovies(nextKey)
                                .flatMap(apiMovie -> handleData(loadType, helper.convertApiDataToLocalData(apiMovie.getResults(), movieTag, movieGenres),
                                        nextKey, apiMovie.getTotalPages()));
                    }
                    return Single.just(new MediatorResult.Success(true));
                });
    }

    @NonNull
    @Override
    public Single<MediatorResult> loadSingle(@NonNull LoadType loadType, @NonNull PagingState<Integer, Movie> pagingState) {
        Single<RemoteKey> remoteKeySource = null;
        switch (loadType) {
            case REFRESH -> {
                remoteKeySource = Single.just(new RemoteKey(1, remoteKeyTag));
            }
            case PREPEND -> {return Single.just(new MediatorResult.Success(true));}
            case APPEND -> {
                Movie movie = pagingState.lastItemOrNull();
                if(movie == null) {
                    return Single.just(new MediatorResult.Success(true));
                }
                remoteKeySource = remoteKeyDao.getKey(remoteKeyTag);
            }
        }
        return loadMoreMovie(remoteKeySource, loadType);
    }
}
