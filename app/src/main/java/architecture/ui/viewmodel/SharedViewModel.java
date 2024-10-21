package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.GenreRepository;
import architecture.domain.BitmapProcessor;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SharedViewModel extends ViewModel {
    private final AuthenticationRepository authRepo;
    private final GenreRepository genreRepo;
    private final MutableLiveData<Boolean> shouldHideBottomNavBar = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> loadingHomeScreenDataState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genreNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> splashState = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> newAvatarDataState = new MutableLiveData<>();
    private boolean loaded = false;

    // getters
    public MutableLiveData<Boolean> getShouldHideBottomNavBar() {return shouldHideBottomNavBar;}
    public MutableLiveData<Boolean> getLoadingHomeScreenDataState() { return loadingHomeScreenDataState; }
    public MutableLiveData<Boolean> getLoginNavigationState() { return loginNavigationState; }
    public MutableLiveData<Boolean> getGenreNavigationState() { return genreNavigationState; }
    public MutableLiveData<Boolean> geSplashState() { return splashState; }
    public MutableLiveData<Bitmap> getImageDataState() { return newAvatarDataState; }

    // setters
    public void setBottomNavBarVisibility(boolean visibility) { shouldHideBottomNavBar.setValue(!visibility); }
    public void setLoadingHomeDataState(boolean state) { loadingHomeScreenDataState.setValue(state); }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void setImageDataState(byte[] data) {
        Single.fromCallable(() -> (new BitmapProcessor()).depressBitmap(data))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newAvatarDataState::setValue, throwable -> Log.d("Debug", throwable.toString()));
    }

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
                checkUserGenres(); },
                throwable -> Log.d("Debug", throwable.toString()));
    }

    @SuppressLint("CheckResult")
    private void checkUserGenres() {
        if(!authRepo.checkIfSignedIn()) {
            loginNavigationState.setValue(true);
            splashState.setValue(true);
            return;
        }
        genreRepo.requestUserGenres(authRepo.getUserUid()).addOnSuccessListener(genres -> {
            splashState.setValue(true);
            if(genres.isEmpty()) {
                genreNavigationState.setValue(true);
                return;
            }
            loadingHomeScreenDataState.setValue(true);
        }).addOnFailureListener(e -> Log.d("Debug", e.toString()));
    }
}
