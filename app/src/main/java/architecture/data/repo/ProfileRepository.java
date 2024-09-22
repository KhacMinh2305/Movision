package architecture.data.repo;
import javax.inject.Inject;
import javax.inject.Singleton;

import architecture.data.local.entity.User;
import architecture.data.source.ProfileDataSource;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class ProfileRepository {

    private final ProfileDataSource dataSource;

    @Inject
    public ProfileRepository(ProfileDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cacheUsername(String username) {
        dataSource.cacheUsername(username);
    }
    public String getUsername() {
        return dataSource.getUsername();
    }

    public Single<User> getUser(String username, String password) {
        return dataSource.getUser(username, password);
    }

    public void insertUser(User user) {
        dataSource.insertUser(user);
    }
}
