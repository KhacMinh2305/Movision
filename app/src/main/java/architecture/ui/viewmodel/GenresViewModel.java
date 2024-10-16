package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import architecture.data.model.genre.Genre;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.GenreRepository;
import architecture.other.AppMessage;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GenresViewModel extends ViewModel {
    private final AuthenticationRepository authRepo;
    private final GenreRepository genreRepo;
    private final List<Genre> userMovieGenres = new ArrayList<>();
    private final MutableLiveData<List<Genre>> appGenres = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finished = new MutableLiveData<>();
    private boolean loadedInitially = false;

    public MutableLiveData<List<Genre>> getAppGenres() { return appGenres; }
    public MutableLiveData<String> getError() {
        return error;
    }
    public MutableLiveData<Boolean> isFinished() { return finished; }

    @Inject
    public GenresViewModel(AuthenticationRepository authRepo, GenreRepository genreRepo) {
        this.authRepo = authRepo;
        this.genreRepo = genreRepo;
    }

    public void loadInitially() {
        if(loadedInitially) {
            return;
        }
        requestMovieGenres();
        loadedInitially = true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void requestMovieGenres() {
        genreRepo.requestMovieGenres().subscribe(appGenres::setValue, throwable -> {});
    }

    public void addGenreToTempList(Genre genre) {
        userMovieGenres.add(genre);
    }

    public void removeGenreFromTempList(Genre genre) {
        userMovieGenres.remove(genre);
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void saveUserGenres() {
        if(userMovieGenres.size() < 3) {
            error.setValue(AppMessage.GENRES_NOT_ENOUGH);
            return;
        }
        genreRepo.pushUserGenresToDB(authRepo.getUserUid(), userMovieGenres).subscribe(task -> {
            task.addOnSuccessListener(runnable -> {
                finished.setValue(true);
                genreRepo.cacheUserGenres(userMovieGenres);
            }).addOnFailureListener(e -> {
                error.setValue(e.getMessage());
            });
        });
    }
}
