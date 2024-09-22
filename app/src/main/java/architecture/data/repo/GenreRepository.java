package architecture.data.repo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.model.genre.Genre;
import architecture.data.source.MovieGenreSource;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class GenreRepository {
    private final MovieGenreSource dataSource;

    @Inject
    public GenreRepository(MovieGenreSource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<Task<DocumentSnapshot>> requestUserGenres(String username) {
        return dataSource.requestUserGenres(username);
    }

    public Single<List<Genre>> requestMovieGenres() {
        return dataSource.requestMovieGenres();
    }

    public void cacheUserGenres(List<Genre> userGenres) {
        dataSource.cacheUserGenres(userGenres);
    }

    public List<Genre> getAppGenres() {
        return dataSource.getAppGenres();
    }
    public List<Genre> getUserGenres() { return dataSource.getUserGenres(); }

    public Single<Task<Void>> pushUserGenresToDB(String username, List<Genre> userGenres) {
        return dataSource.pushUserGenresToDB(username, userGenres);
    }

    public Task<Void> updateUserGenres(String username, List<Genre> genres) {
        return dataSource.updateUserGenres(username, genres);
    }
}
