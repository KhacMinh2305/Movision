package architecture.data.source;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.AppDataStore;
import architecture.data.model.genre.Genre;
import architecture.data.network.api.TmdbServices;
import architecture.domain.ListProcessingHelper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class MovieGenreSource {

    private final FirebaseFirestore cloud;
    private final AppDataStore dataStore;
    private final TmdbServices tmdbService;
    private List<Genre> appGenres = new ArrayList<>();
    private final List<Genre> userGenres = new ArrayList<>();

    public List<Genre> getAppGenres() {
        return appGenres;
    }
    public List<Genre> getUserGenres() {return userGenres;}

    // setters
    public void cacheUserGenres(List<Genre> genres) {
        this.userGenres.clear();
        this.userGenres.addAll(genres);
    }

    @Inject
    public MovieGenreSource(AppDataStore dataStore, FirebaseFirestore cloud, TmdbServices tmdbService) {
        this.cloud = cloud;
        this.dataStore = dataStore;
        this.tmdbService = tmdbService;
    }

    public Single<Task<DocumentSnapshot>> requestUserGenres(String username) {
        return Single.fromCallable(() -> {
            appGenres.addAll(requestMovieGenres().blockingGet());
            return true;
        }).flatMap(var -> Single.just(cloud.collection("user_genres").document(username).get()))
                .subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Genre>> requestMovieGenres() {
        return (!appGenres.isEmpty()) ? Single.just(appGenres) :
                tmdbService.getMovieGenres().map(genres -> {
                    appGenres = genres.getGenres();
                    return appGenres;
                });
    }

    /** @noinspection MismatchedQueryAndUpdateOfCollection*/
    public Single<Task<Void>> pushUserGenresToDB(String username, List<Genre> userGenres) {
        return Single.fromCallable(() -> {
            List<Long> userGenresId = new ArrayList<>();
            for(Genre genre : userGenres) {
                userGenresId.add(genre.getId());
            }
            return userGenresId;
        }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ids -> cloud.collection("user_genres").document(username)
                .set(createNewUserGenresRecord(username, ids), SetOptions.merge()));
    }

    /** @noinspection MismatchedQueryAndUpdateOfCollection*/
    private Map<String, Object> createNewUserGenresRecord(String username, List<Long> userGenres) {
        Map<String, Object> userGenresMap = new HashMap<>();
        userGenresMap.put("genresId", userGenres);
        userGenresMap.put("username", username);
        return userGenresMap;
    }

    public Task<Void> updateUserGenres(String username, List<Genre> genres) {
        ListProcessingHelper helper = new ListProcessingHelper();
        return cloud.collection("user_genres").document(username).update("genresId", helper.retrieveListIds(genres));
    }
}

// bugs :
