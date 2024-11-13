package architecture.data.source.other;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import java.util.ArrayList;
import java.util.List;
import architecture.data.model.movie.category.ApiDiscoverMovie;
import architecture.data.model.movie.in_app.DiscoverMovieItem;
import architecture.data.model.movie.result.ApiDiscoverMovieResult;
import architecture.data.network.api.TmdbServices;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DiscoverMovieSource extends RxPagingSource<Integer, DiscoverMovieItem> {

    private final TmdbServices services;
    private final Float minRate;
    private final Float maxRate;
    private final Integer minVoteCount;
    private final Integer maxVoteCount;
    private final String genresId;
    private final Integer year;

    public DiscoverMovieSource(TmdbServices services, Float minRate, Float maxRate,
                               Integer minVoteCount, Integer maxVoteCount, String genresId,
                               Integer year) {
        this.services = services;
        this.minRate = minRate;
        this.maxRate = maxRate;
        this.minVoteCount = minVoteCount;
        this.maxVoteCount = maxVoteCount;
        this.genresId = genresId;
        this.year = year;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, DiscoverMovieItem>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer loadKey = loadParams.getKey();
        if(loadKey == null) loadKey = 1;
        return services.loadDiscoverMovie(minRate, maxRate, minVoteCount, maxVoteCount, genresId, year, loadKey)
                .subscribeOn(Schedulers.single()).map(this::toLoadResult);
    }

    private LoadResult<Integer, DiscoverMovieItem> toLoadResult(ApiDiscoverMovie apiDiscoverMovie) {
        List<DiscoverMovieItem> result = new ArrayList<>();
        for(ApiDiscoverMovieResult res : apiDiscoverMovie.getResults()) {
            result.add(new DiscoverMovieItem(res.getId(),
                    res.getTitle(), res.getOverview(),
                    res.getVoteAverage(), res.getPosterPath()));
        }
        int currentPage = apiDiscoverMovie.getPage();
        Integer nextKey = (apiDiscoverMovie.getPage() < apiDiscoverMovie.getTotalPages()) ? currentPage + 1 : null;
        return new LoadResult.Page<>(result, null, nextKey,
                LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, DiscoverMovieItem> pagingState) {
        Integer position = pagingState.getAnchorPosition();
        if(position == null) return null;
        LoadResult.Page<Integer, DiscoverMovieItem> page = pagingState.closestPageToPosition(position);
        if(page == null) return null;
        Integer preKey = page.getPrevKey();
        if(preKey != null) return preKey + 1;
        Integer nextKey = page.getNextKey();
        if(nextKey != null) return nextKey - 1;
        return null;
    }
}
