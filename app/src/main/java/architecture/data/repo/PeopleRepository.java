package architecture.data.repo;
import androidx.paging.Pager;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.entity.People;
import architecture.data.source.PeopleDataSource;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class PeopleRepository {

    private final PeopleDataSource dataSource;

    @Inject
    public PeopleRepository(PeopleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<List<People>> loadListPopularPeople() {
        return dataSource.loadListPopularPeople();
    }

    public Pager<Integer, People> getPeoplePager(int gender) {
        return dataSource.getPeoplePager(gender);
    }
}
