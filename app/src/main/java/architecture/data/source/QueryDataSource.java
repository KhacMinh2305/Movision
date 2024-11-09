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
    private static final int HISTORY_LIMIT = 50;
    private final LocalDatabase db;
    private final ProfileDataSource profileSource;
    private final SearchHistoryDao historyDao;
    private final List<SearchQuery> memCachedList;

    @Inject
    public QueryDataSource(LocalDatabase db, ProfileDataSource profileSource, SearchHistoryDao historyDao) {
        this.db = db;
        this.profileSource = profileSource;
        this.historyDao = historyDao;
        memCachedList = new ArrayList<>();
    }

    public Single<List<SearchQuery>> getSearchQueriesHistory() {
        if(!memCachedList.isEmpty()) {
            return Single.just(memCachedList);
        }
        return historyDao.getAllHistory(profileSource.getUserUid()).subscribeOn(Schedulers.io())
                .map(searchQueries -> {
                    for(int i = searchQueries.size() - 1; i >= 0; i--) {
                        memCachedList.add(searchQueries.get(i));
                    }
                    return memCachedList;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> memCachedList);
    }

    private SearchQuery createSearchQuery(String userId, String query) {
        long id = System.currentTimeMillis();
        return new SearchQuery(id, userId, query);
    }

    public Single<SearchQuery> addSearchQuery(String query) {
        SearchQuery searchQuery = createSearchQuery(profileSource.getUserUid(), query);
        return Single.fromCallable(() -> {
            db.runInTransaction(() -> {
                int totalRecord = memCachedList.size();
                if(totalRecord >= HISTORY_LIMIT) {
                    historyDao.deleteQuery(memCachedList.get(memCachedList.size() - 1).id);
                }
                historyDao.insertQuery(searchQuery);
                memCachedList.add(0, searchQuery);
            });
            return searchQuery;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteSearchQuery(long id) {
        return Completable.fromAction(() -> db.runInTransaction(() -> {
            historyDao.deleteQuery(id);
            memCachedList.removeIf(searchQuery -> searchQuery.id == id);
        })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteAllSearchQueries() {
        Completable.fromAction(() -> db.runInTransaction(() -> {
                    historyDao.deleteAllHistory(profileSource.getUserUid());
                    memCachedList.clear();
                }))
                .subscribeOn(Schedulers.io()).subscribe();
    }
}
