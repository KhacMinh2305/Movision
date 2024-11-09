package architecture.data.source.other;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import java.util.ArrayList;
import java.util.List;
import architecture.data.model.movie.in_app.MovieItem;
import architecture.data.model.movie.result.ApiSearchMovieResult;
import architecture.data.network.api.TmdbServices;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchMovieSource extends RxPagingSource<Integer, MovieItem> {

    private final TmdbServices movieService;
    private final CachingSource cachingSource;
    private String query;
    private List<CachingSource.MovieItemCache> cachedList;

    public SearchMovieSource(TmdbServices movieService, CachingSource cachingSource, String query) {
        this.movieService = movieService;
        this.cachingSource = cachingSource;
        this.query = query.trim().toUpperCase();
    }

    public void init() {
        cachedList = cachingSource.getSearchMovieCachedResult(this.query);
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, MovieItem>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer loadKey = loadParams.getKey();
        if(loadKey == null) loadKey = 1;
        int currentKey = loadKey;
        if(cachedList != null && !cachedList.isEmpty()) {
            if(cachedList.get(cachedList.size() - 1).page() >= loadKey) {
                CachingSource.MovieItemCache itemCache = cachedList.get(loadKey - 1);
                Integer nextKey = (itemCache.page() < itemCache.totalPages()) ? itemCache.page() + 1 : null;
                return Single.just(itemCache.data()).subscribeOn(Schedulers.computation())
                        .map(movieItems -> toResult(nextKey, movieItems));
            }
        }
        return movieService.loadMovieWithQuery(query, loadKey).subscribeOn(Schedulers.single())
                .map(apiSearchMovie -> {
                    List<ApiSearchMovieResult> results = apiSearchMovie.getResults();
                    List<MovieItem> itemList = new ArrayList<>();
                    for(ApiSearchMovieResult result : results) {
                        itemList.add(result.toMovieItem());
                    }
                    cachingSource.cacheSearchMovieQuery(query, itemList, currentKey, apiSearchMovie.getTotalPages());
                    Integer nextKey = (currentKey <= apiSearchMovie.getTotalPages()) ? currentKey + 1 : null;
                    return toResult(nextKey, itemList);
                });
    }

    private LoadResult<Integer, MovieItem> toResult(Integer nextKey, List<MovieItem> result) {
        return new LoadResult.Page<>(result, null, nextKey,
                LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, MovieItem> pagingState) {
        Integer position = pagingState.getAnchorPosition();
        if(position == null) return null;
        LoadResult.Page<Integer, MovieItem> page = pagingState.closestPageToPosition(position);
        if(page == null) return null;
        Integer preKey = page.getPrevKey();
        if(preKey != null) return preKey + 1;
        Integer nextKey = page.getNextKey();
        if(nextKey != null) return nextKey - 1;
        return null;
    }
}
