package architecture.data.source;
import android.util.Log;

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
    private final TmdbServices tmdbService;
    private List<Genre> appGenres = new ArrayList<>();
    private final List<Genre> userGenres = new ArrayList<>();

    public List<Genre> getAppGenres() { return appGenres; }
    public List<Genre> getUserGenres() {return userGenres;}

    // setters
    public void cacheUserGenres(List<Genre> genres) {
        this.userGenres.clear();
        this.userGenres.addAll(genres);
    }

    @Inject
    public MovieGenreSource(FirebaseFirestore cloud, TmdbServices tmdbService) {
        this.cloud = cloud;
        this.tmdbService = tmdbService;
    }

    public Single<Task<DocumentSnapshot>> requestUserGenres(String userId) {
        return Single.just(cloud.collection("user_genres").document(userId).get())
                .subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Genre>> requestMovieGenres() {
        return (!appGenres.isEmpty())
                ? Single.just(appGenres).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                : tmdbService.getMovieGenres().subscribeOn(Schedulers.single()).map(genres -> {
                    appGenres .addAll(genres.getGenres());
                    return appGenres;
                }).observeOn(AndroidSchedulers.mainThread());
    }

    /** @noinspection MismatchedQueryAndUpdateOfCollection*/
    public Single<Task<Void>> pushUserGenresToDB(String userId, List<Genre> userGenres) {
        return Single.fromCallable(() -> {
            List<Long> userGenresId = new ArrayList<>();
            for(Genre genre : userGenres) {
                userGenresId.add(genre.getId());
            }
            return userGenresId;
        }).subscribeOn(Schedulers.computation())
                .map(ids -> cloud.collection("user_genres").document(userId)
                .set(createNewUserGenresRecord(userId, ids), SetOptions.merge()))
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /** @noinspection MismatchedQueryAndUpdateOfCollection*/
    private Map<String, Object> createNewUserGenresRecord(String userId, List<Long> userGenres) {
        Map<String, Object> userGenresMap = new HashMap<>();
        userGenresMap.put("username", userId);
        userGenresMap.put("genresId", userGenres);
        return userGenresMap;
    }

    public Task<Void> updateUserGenres(String userId, List<Genre> genres) {
        ListProcessingHelper helper = new ListProcessingHelper();
        return cloud.collection("user_genres")
                .document(userId)
                .update("genresId", helper.retrieveListIds(genres));
    }
}

// bugs :
