package architecture.data.repo;
import javax.inject.Inject;
import javax.inject.Singleton;

import architecture.data.source.KeyDataSource;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class KeyRepository {
    private final KeyDataSource dataSource;

    @Inject
    public KeyRepository(KeyDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<String> requestToken() {
        return dataSource.requestToken();
    }

    public Single<String> requestSessionId(String username, String password) {
        return dataSource.requestSessionId(username, password);
    }

    public void saveAccountInfo(String username) {
        dataSource.saveAccountInfo(username);
    }

    public void saveSessionId(String sessionId) {
        dataSource.saveSessionId(sessionId);
    }

    public Single<String> getSessionId() {
        return dataSource.getSessionId();
    }

    public void clearLoginInfoOnFailure() {
        dataSource.clearLoginInfoOnFailure();
    }
}
