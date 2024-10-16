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
}
