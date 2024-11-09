package architecture.data.source.other;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import java.util.ArrayList;
import java.util.List;
import architecture.data.model.people.ApiPeopleResult;
import architecture.data.model.people.PeopleItem;
import architecture.data.network.api.TmdbServices;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPeopleSource extends RxPagingSource<Integer, PeopleItem> {

    private final TmdbServices movieService;
    private final CachingSource cachingSource;
    private String query;
    private List<CachingSource.PeopleItemCache> cachedList;

    public SearchPeopleSource(TmdbServices movieService, CachingSource cachingSource, String query) {
        this.movieService = movieService;
        this.cachingSource = cachingSource;
        this.query = query.trim().toUpperCase();
    }

    public void init() {
        cachedList = cachingSource.getSearchPeopleCachedResult(this.query);
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, PeopleItem>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer loadKey = loadParams.getKey();
        if(loadKey == null) loadKey = 1;
        int currentKey = loadKey;
        if(cachedList != null && !cachedList.isEmpty()) {
            if(cachedList.get(cachedList.size() - 1).page() >= loadKey) {
                CachingSource.PeopleItemCache itemCache = cachedList.get(loadKey - 1);
                Integer nextKey = (itemCache.page() < itemCache.totalPages()) ? itemCache.page() + 1 : null;
                return Single.just(itemCache.data()).subscribeOn(Schedulers.computation())
                        .map(peopleItems -> toResult(nextKey, peopleItems));
            }
        }

        return movieService.loadPeopleWithQuery(query, loadKey).subscribeOn(Schedulers.single())
                .map(apiSearchPeople -> {
                    List<ApiPeopleResult> results = apiSearchPeople.getResults();
                    List<PeopleItem> itemList = new ArrayList<>();
                    for(ApiPeopleResult result : results) {
                        itemList.add(result.toPeopleItem());
                    }
                    cachingSource.cacheSearchPeopleQuery(query, itemList, currentKey, apiSearchPeople.getTotalPages());
                    Integer nextKey = (currentKey <= apiSearchPeople.getTotalPages()) ? currentKey + 1 : null;
                    return toResult(nextKey, itemList);
                });
    }

    private LoadResult<Integer, PeopleItem> toResult(Integer nextKey, List<PeopleItem> result) {
        return new LoadResult.Page<>(result, null, nextKey,
                LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, PeopleItem> pagingState) {
        Integer position = pagingState.getAnchorPosition();
        if(position == null) return null;
        LoadResult.Page<Integer, PeopleItem> page = pagingState.closestPageToPosition(position);
        if(page == null) return null;
        Integer preKey = page.getPrevKey();
        if(preKey != null) return preKey + 1;
        Integer nextKey = page.getNextKey();
        if(nextKey != null) return nextKey - 1;
        return null;
    }
}
