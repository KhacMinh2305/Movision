package architecture.data.repo;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.entity.SearchQuery;
import architecture.data.source.QueryDataSource;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class QueryRepository {

    private final QueryDataSource querySource;

    @Inject
    public QueryRepository(QueryDataSource querySource) {
        this.querySource = querySource;
    }

    public Single<List<SearchQuery>> getSearchQueriesHistory() {
        return querySource.getSearchQueriesHistory();
    }

    public Single<SearchQuery> addSearchQuery(String query) {
        return querySource.addSearchQuery(query);
    }

    public Completable deleteSearchQuery(long id) {
        return querySource.deleteSearchQuery(id);
    }

    public Completable clearSearchHistory() {
        return querySource.clearSearchHistory();
    }
}
