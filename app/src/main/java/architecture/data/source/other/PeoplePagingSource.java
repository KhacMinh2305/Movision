package architecture.data.source.other;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import java.util.ArrayList;
import java.util.List;
import architecture.data.local.entity.People;
import architecture.data.model.people.ApiPeopleResult;
import architecture.data.model.people.ApiPeople;
import architecture.data.network.api.TmdbServices;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PeoplePagingSource extends RxPagingSource<Integer, People> {

    private List<People> cachedList;
    private Integer currentApiKey;
    private int totalPages;

    private final DataContainer data;
    private static final int OFFSET = 20;
    private final TmdbServices apiService;
    private int gender = -1;
    private String tag;

    public PeoplePagingSource(TmdbServices apiService, int genderFilter, String tag, DataContainer dataSet) {
        this.apiService = apiService;
        gender = genderFilter;
        this.tag = tag;
        this.data = dataSet;
        init();
    }

    private void init() {
        cachedList = data.getCachedList();
        currentApiKey = data.getCurrentApiKey();
        totalPages = data.getTotalPages();
    }

    private Single<ApiPeople> getApiCallWithTag(Integer nextKey) {
        return (tag.equals(AppConstant.POPULAR_PEOPLE_TAG))
                ? apiService.loadPopularPeople(nextKey)
                : apiService.loadTrendingPeople(nextKey);
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, People>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        Integer nextKey = loadParams.getKey();
        if(nextKey == null) {
            nextKey = 1;
        }
        // if all the results are loaded
        if (totalPages != 0 && nextKey > totalPages) {
            return Single.just(toEmptyResult()).subscribeOn(Schedulers.io());
        }
        // if data is loaded , get from cache
        if(currentApiKey > nextKey) {
            return Single.just(loadCachedResult(nextKey)).subscribeOn(Schedulers.io());
        }

        // otherwise , request from api
        return getApiCallWithTag(nextKey)
                .subscribeOn(Schedulers.single())
                .map(this::toRemoteResult);
    }

    private LoadResult<Integer, People> toEmptyResult() {
        return new LoadResult.Page<>(new ArrayList<>(), null, totalPages + 1,
                LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    private List<People> copyPortionOfCache(int beginIndex, int exclusiveEndIndex) {
        List<People> list = new ArrayList<>();
        for(int i = beginIndex; i < exclusiveEndIndex; i++) {
            if(cachedList.get(i).gender == this.gender || this.gender == AppConstant.HUMAN_ALL) {
                list.add(cachedList.get(i));
            }
        }
        return list;
    }

    private LoadResult<Integer, People> loadCachedResult(int currentKey) {
        int beginIndex = OFFSET * (currentKey - 1);
        int exclusiveEndIndex = (currentKey < totalPages) ?  OFFSET * currentKey : cachedList.size() - beginIndex;
        return new LoadResult.Page<>(copyPortionOfCache(beginIndex, exclusiveEndIndex),
                null, currentKey + 1, LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    private LoadResult<Integer, People> toRemoteResult(ApiPeople apiPopularPeople) {
        totalPages = apiPopularPeople.getTotalPages();
        currentApiKey = apiPopularPeople.getPage() + 1;
        data.updateKey(currentApiKey);
        List<ApiPeopleResult> listResult = apiPopularPeople.getResults();
        List<People> providedList = new ArrayList<>();
        for(ApiPeopleResult result : listResult) {
            People people = result.toPeople();
            cachedList.add(people);
            if(people.gender == this.gender || this.gender == AppConstant.HUMAN_ALL) {
                providedList.add(result.toPeople());
            }
        }
        return new LoadResult.Page<>(providedList,
                null, apiPopularPeople.getPage() + 1, LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, People> pagingState) {
        Integer position = pagingState.getAnchorPosition();
        if(position == null) {
            return null;
        }
        LoadResult.Page<Integer, People> anchoredPage = pagingState.closestPageToPosition(position);
        if(anchoredPage == null) {
            return null;
        }
        Integer prevKey = anchoredPage.getPrevKey();
        if(prevKey != null) {
            return prevKey + 1;
        }
        Integer nextKey = anchoredPage.getNextKey();
        if(nextKey != null) {
            return nextKey - 1;
        }
        return null;
    }

    public static class DataContainer {
        private List<People> cachedList = new ArrayList<>();
        private Integer currentApiKey = 1;
        private int totalPages = 0;

        public List<People> getCachedList() { return cachedList; }
        public Integer getCurrentApiKey() { return currentApiKey; }
        public int getTotalPages() { return totalPages; }

        public void updateKey(int key) {
            currentApiKey = key;
        }
    }
}
