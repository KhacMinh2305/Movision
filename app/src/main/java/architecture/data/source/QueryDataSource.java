package architecture.data.source;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.SearchHistoryDao;
import architecture.data.local.entity.SearchQuery;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class QueryDataSource {
    private final LocalDatabase db;
    private final SearchHistoryDao historyDao;
    private final List<SearchQuery> memCachedList;

    @Inject
    public QueryDataSource(LocalDatabase db, SearchHistoryDao historyDao) {
        this.db = db;
        this.historyDao = historyDao;
        memCachedList = new ArrayList<>();
    }

    public Single<List<SearchQuery>> getSearchQueriesHistory() {
        if(!memCachedList.isEmpty()) {
            return Single.just(memCachedList);
        }
        return historyDao.getAllHistory().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(searchQueries -> {
                    memCachedList.clear();
                    memCachedList.addAll(searchQueries);
                })
                .onErrorReturn(throwable -> memCachedList);
    }

    private SearchQuery createSearchQuery(String userId, String query, String tag) {
        long id = System.currentTimeMillis();
        return new SearchQuery(id, userId, query, tag);
    }

    public void addSearchQuery(String userId, String query, String tag) {
        SearchQuery searchQuery = createSearchQuery(userId, query, tag);
        Completable.fromAction(() -> db.runInTransaction(() -> historyDao.insertQuery(searchQuery)))
                .subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteSearchQuery(long id) {
        Completable.fromAction(() -> db.runInTransaction(() -> historyDao.deleteQuery(id)))
                .subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteAllSearchQueries() {
        Completable.fromAction(() -> db.runInTransaction(historyDao::deleteAllHistory))
                .subscribeOn(Schedulers.io()).subscribe();
    }
}
