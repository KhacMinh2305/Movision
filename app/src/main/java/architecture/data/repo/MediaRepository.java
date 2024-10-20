package architecture.data.repo;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.source.MediaDataSource;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class MediaRepository {

    private final MediaDataSource dataSource;

    @Inject
    public MediaRepository(MediaDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PublishSubject<Map<Integer, String>> pushUserAvatarToStorage(String userId, byte[] bytes) {
        return dataSource.pushUserAvatarToStorage(userId, bytes);
    }

}
