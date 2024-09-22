package architecture.data.source;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.AppDataStore;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.UserDao;
import architecture.data.local.entity.User;
import architecture.data.network.api.TmdbServices;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class ProfileDataSource {
    private final FirebaseFirestore cloud;
    private final AppDataStore dataStore;
    private final LocalDatabase db;
    private final TmdbServices tmdbService;
    private final UserDao userDao;
    private String username;

    // caches
    public String getUsername() {
        return username;
    }

    public void cacheUsername(String username) {
        this.username = username;
    }

    @Inject
    public ProfileDataSource(AppDataStore dataStore, LocalDatabase db, FirebaseFirestore cloud,
                             TmdbServices tmdbService, UserDao userDao) {
        this.cloud = cloud;
        this.dataStore = dataStore;
        this.db = db;
        this.tmdbService = tmdbService;
        this.userDao = userDao;
    }

    public void insertUser(User user) {
        Completable.fromAction(() -> {
            db.runInTransaction(() -> {
                userDao.addUser(user);
            });
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public Single<User> getUser(String username, String password) {
        return userDao.getUser(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> new User("", "", ""));
    }

}
