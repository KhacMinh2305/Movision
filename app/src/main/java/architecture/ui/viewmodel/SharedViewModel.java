package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Tasks;
import javax.inject.Inject;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.GenreRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SharedViewModel extends ViewModel {
    private final AuthenticationRepository authRepo;
    private final GenreRepository genreRepo;
    private final MutableLiveData<Boolean> shouldHideBottomNavBar = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> loadingHomeScreenDataState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genreNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> splashState = new MutableLiveData<>();
    private boolean loaded = false;

    // getters
    public MutableLiveData<Boolean> getShouldHideBottomNavBar() {return shouldHideBottomNavBar;}
    public MutableLiveData<Boolean> getLoadingHomeScreenDataState() { return loadingHomeScreenDataState; }
    public MutableLiveData<Boolean> getLoginNavigationState() { return loginNavigationState; }
    public MutableLiveData<Boolean> getGenreNavigationState() { return genreNavigationState; }
    public MutableLiveData<Boolean> geSplashState() { return splashState; }

    // setters
    public void setBottomNavBarVisibility(boolean visibility) { shouldHideBottomNavBar.setValue(!visibility); }
    public void setLoadingHomeDataState(boolean state) { loadingHomeScreenDataState.setValue(state); }

    @Inject
    public SharedViewModel(AuthenticationRepository authRepo, GenreRepository genreRepo) {
        this.authRepo = authRepo;
        this.genreRepo = genreRepo;
    }

    public void loadInitially() {
        if(loaded) { return; }
        loadGenresAndCheckUserGenres();
        loaded = true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadGenresAndCheckUserGenres() {
        genreRepo.requestMovieGenres().subscribe(genres -> {
            checkUserGenres();
        }, throwable -> Log.d("Debug", "Something went wrong !"));
    }

    /** @noinspection ResultOfMethodCallIgnored */
    @SuppressLint("CheckResult")
    private void checkUserGenres() {
        if(!authRepo.checkIfSignedIn()) {
            loginNavigationState.setValue(true);
            splashState.setValue(true);
            return;
        }
        genreRepo.requestUserGenres(authRepo.getUserUid()).subscribe(documentSnapshotTask -> {
            // chuyen cai nay ve DataSource
            documentSnapshotTask.onSuccessTask(documentSnapshot -> {
                splashState.setValue(true);
                if(!documentSnapshot.exists()) {
                    genreNavigationState.setValue(true);
                } else {
                    loadingHomeScreenDataState.setValue(true);
                }
                return Tasks.forResult(null);
            });
            // notify to load data from home
            Log.d("Debug", "Load Home Data");
        }, throwable -> {
            Log.d("Debug", "ERROR !");});
    }
}
