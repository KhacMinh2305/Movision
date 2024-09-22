package architecture.data.source;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.RemoteKey;
import architecture.data.model.movie.result.ApiMovieResult;
import architecture.data.network.api.TmdbServices;
import architecture.data.network.other.CategoryMovieRemoteMediator;
import architecture.domain.MovieConversionHelper;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class MovieDataSource {
    private final LocalDatabase db;
    private final TmdbServices movieService;
    private final MovieDao movieDao;
    private final RemoteKeyDao keyDao;
    private final MovieGenreSource genreSource;

    @Inject
    public MovieDataSource(LocalDatabase db, TmdbServices movieService,
                           MovieDao movieDao, RemoteKeyDao keyDao,
                           MovieGenreSource genreSource) {
        this.movieService = movieService;
        this.db = db;
        this.movieDao = movieDao;
        this.keyDao = keyDao;
        this.genreSource = genreSource;
    }

    // --------------------------------------load data for preview recycler views in home fragments--------------------------------------
    public Single<List<Movie>> loadPreviewDataByCategory(String category) {
        switch (category) {
            case AppConstant.CATEGORY_UPCOMING_TITLE -> {
                return loadPreviewUpcomingCategory();
            }
            case AppConstant.CATEGORY_TRENDING_TITLE -> {
                return loadPreviewTrendingCategory();
            }
            case AppConstant.CATEGORY_TOP_RATED_TITLE -> {
                return loadPreviewTopRatedCategory();
            }
            case AppConstant.CATEGORY_POPULAR_TITLE -> {
                return loadPreviewPopularCategory();
            }
            case AppConstant.CATEGORY_PLAYING_TITLE -> {
                return loadPreviewNowPlayingCategory();
            }
            default -> {
                return Single.just((List<Movie>) new ArrayList<Movie>())
                        .subscribeOn(Schedulers.single())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
    }

    private Single<List<Movie>> loadPreviewUpcomingCategory() {
        return movieService.loadPreviewUpcomingMovie(1)
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_UPCOMING_TAG))
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Single<List<Movie>> loadPreviewTrendingCategory() {
        return movieService.loadPreviewTrendingCategory("day")
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_TRENDING_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewTopRatedCategory() {
        return movieService.loadPreviewTopRatedCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_TOP_RATED_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewPopularCategory() {
        return movieService.loadPreviewPopularCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_POPULAR_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewNowPlayingCategory() {
        return movieService.loadPreviewPlayingCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_PLAYING_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Movie> handleData(List<ApiMovieResult> apiData, int totalPages, String tag) {
        List<Movie> data = (new MovieConversionHelper()).convertApiDataToLocalData(apiData, tag, genreSource.getAppGenres()); // process raw data
        int nextKey = (totalPages == 1) ? 1 : 2;
        cacheData(data, tag, nextKey);
        return data;
    }

    private void cacheData(List<Movie> result, String tag, int nextKey) {
        db.runInTransaction(() -> {
            // refresh data and key
            keyDao.deleteRemoteKeys(tag);
            movieDao.deleteMovies(tag);
            // insert data (note the case api only has 1 page of data. This cause error when load append in MovieRemoteMediator)
            RemoteKey remoteKey = new RemoteKey(nextKey, tag);
            keyDao.insertRemoteKey(remoteKey);
            movieDao.insertMovies(result);
        });
    }

    // --------------------------------------load movie with genre--------------------------------------
    public Single<List<Movie>> loadMoviesByGenre(Map<String, Object> filters) {
        return movieService.loadMovieByGenre(filters)
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_UNDEFINED_TAG));
    }

    // --------------------------------------load movie with genre--------------------------------------
    public Pager<Integer, Movie> getMoviePager(String tag, int pageSize) {
        CategoryMovieRemoteMediator movieMediator =
                new CategoryMovieRemoteMediator(db, movieService, movieDao, keyDao, genreSource.getAppGenres());
        movieMediator.setMovieInfo(tag);
        Pager<Integer, Movie> pager = new Pager(new PagingConfig(pageSize), null,
                movieMediator,
                () -> movieDao.moviePagingSource(tag));
        return pager;
    }
}
