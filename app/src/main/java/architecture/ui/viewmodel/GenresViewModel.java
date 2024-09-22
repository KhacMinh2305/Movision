package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import architecture.data.model.genre.Genre;
import architecture.data.repo.GenreRepository;
import architecture.data.repo.ProfileRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GenresViewModel extends ViewModel {
    private final ProfileRepository profileRepo;
    private final GenreRepository genreRepo;
    private final List<Genre> userMovieGenres = new ArrayList<>();
    private final MutableLiveData<List<Genre>> appGenres = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finished = new MutableLiveData<>();

    public MutableLiveData<List<Genre>> getAppGenres() { return appGenres; }
    public MutableLiveData<String> getError() {
        return error;
    }
    public MutableLiveData<Boolean> isFinished() {
        return finished;
    }

    @Inject
    public GenresViewModel(ProfileRepository profileRepo, GenreRepository genreRepo) {
        this.profileRepo = profileRepo;
        this.genreRepo = genreRepo;
        requestMovieGenres();
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
            error.setValue("You must chose at least 3 genres to continue !");
            return;
        }
        genreRepo.pushUserGenresToDB(profileRepo.getUsername(), userMovieGenres).subscribe(task -> {
            task.addOnSuccessListener(runnable -> {
                finished.setValue(true);
                genreRepo.cacheUserGenres(userMovieGenres);
            }).addOnFailureListener(runnable -> {
                error.setValue("Something went wrong !");
            });
        });
    }
}
