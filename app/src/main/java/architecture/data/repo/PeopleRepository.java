package architecture.data.repo;
import androidx.paging.Pager;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.entity.People;
import architecture.data.model.people.Caster;
import architecture.data.source.PeopleDataSource;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class PeopleRepository {

    private final PeopleDataSource dataSource;

    @Inject
    public PeopleRepository(PeopleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<List<People>> loadListTrendingPeople() {
        return dataSource.loadListTrendingPeople();
    }

    public Single<List<People>> loadListPopularPeople() {
        return dataSource.loadListPopularPeople();
    }

    public Pager<Integer, People> getPeoplePager(int gender, String tag) {
        return dataSource.getPeoplePager(gender, tag);
    }

    public Single<List<Caster>> loadMovieCasters(int movieId) {
        return dataSource.loadMovieCasters(movieId);
    }
}
