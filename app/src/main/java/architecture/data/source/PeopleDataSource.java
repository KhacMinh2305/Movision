package architecture.data.source;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.PeopleDao;
import architecture.data.local.entity.People;
import architecture.data.network.api.TmdbServices;
import architecture.data.source.other.PeoplePagingSource;
import architecture.domain.PeopleConversionHelper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class PeopleDataSource {
    private final LocalDatabase db;
    private final TmdbServices apiService;
    private final PeopleDao peopleDao;

    @Inject
    public PeopleDataSource(LocalDatabase db, TmdbServices apiService, PeopleDao peopleDao) {
        this.db = db;
        this.apiService = apiService;
        this.peopleDao = peopleDao;
    }

    public Single<List<People>> loadListPopularPeople() {
        return apiService.loadPopularPeople(1)
                .subscribeOn(Schedulers.single())
                .map(apiPopularPeople -> {
                    List<People> people = (new PeopleConversionHelper())
                            .convertApiDataToLocalData(apiPopularPeople.getResults());
                    cacheToDb(people);
                    return people;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void cacheToDb(List<People> people) {
        db.runInTransaction(() -> {
            peopleDao.insertPeople(people);
        });
    }

    public Pager<Integer, People> getPeoplePager(int gender) {
        Pager<Integer, People> pager = new Pager<>(
                new PagingConfig(20),
                () -> new PeoplePagingSource(apiService, gender));
        return pager;
    }
}
