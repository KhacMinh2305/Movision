package architecture.data.repo;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.source.MediaDataSource;
import io.reactivex.rxjava3.core.Single;
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

    public Single<List<String>> loadPersonImages(int personId) {
        return dataSource.loadPersonImages(personId);
    }

}
