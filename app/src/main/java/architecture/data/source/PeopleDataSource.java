package architecture.data.source;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.PeopleDao;
import architecture.data.local.entity.People;
import architecture.data.network.api.TmdbServices;
import architecture.data.source.other.PeoplePagingSource;
import architecture.domain.PeopleConversionHelper;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class PeopleDataSource {
    private final LocalDatabase db;
    private final TmdbServices apiService;
    private final PeopleDao peopleDao;
    private Map<String, PeoplePagingSource.DataContainer> pagingSourceMap;

    @Inject
    public PeopleDataSource(LocalDatabase db, TmdbServices apiService, PeopleDao peopleDao) {
        this.db = db;
        this.apiService = apiService;
        this.peopleDao = peopleDao;
        init();
    }

    private void init() {
        pagingSourceMap = new HashMap<>();
        pagingSourceMap.put(AppConstant.POPULAR_PEOPLE_TAG, new PeoplePagingSource.DataContainer());
        pagingSourceMap.put(AppConstant.TRENDING_PEOPLE_TAG, new PeoplePagingSource.DataContainer());
    }

    public Single<List<People>> loadListTrendingPeople() {
        return apiService.loadTrendingPeople(1)
                .subscribeOn(Schedulers.single())
                .map(apiPeople -> {
                    List<People> people = (new PeopleConversionHelper())
                            .convertApiDataToLocalData(apiPeople.getResults());
                    cacheToDb(people);
                    return people;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<People>> loadListPopularPeople() {
        return apiService.loadPopularPeople(1)
                .subscribeOn(Schedulers.single())
                .map(apiPeople -> {
                    List<People> people = (new PeopleConversionHelper())
                            .convertApiDataToLocalData(apiPeople.getResults());
                    cacheToDb(people);
                    return people;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void cacheToDb(List<People> people) {
        db.runInTransaction(() -> peopleDao.insertPeople(people));
    }

    public Pager<Integer, People> getPeoplePager(int gender, String tag) {
        PeoplePagingSource.DataContainer dataSet = pagingSourceMap.get(tag);
        Pager<Integer, People> pager = new Pager<>(
                new PagingConfig(20),
                () -> new PeoplePagingSource(apiService, gender, tag, dataSet));
        return pager;
    }
}
