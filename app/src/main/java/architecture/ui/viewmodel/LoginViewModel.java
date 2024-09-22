package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import architecture.data.local.entity.User;
import architecture.data.model.genre.Genre;
import architecture.data.repo.GenreRepository;
import architecture.data.repo.KeyRepository;
import architecture.data.repo.ProfileRepository;
import architecture.domain.HashingHelper;
import architecture.domain.ListProcessingHelper;
import architecture.other.AppConstant;
import architecture.other.TaskManager;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private static final String USER_CLICK_BUTTON_ALLOW = "click_allow";
    private static final String LOGIN_SCREEN_STOP = "screen_stop";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private final SavedStateHandle stateHandle;
    private final KeyRepository keyRepo;
    private final ProfileRepository profileRepo;
    private final GenreRepository genreRepo;
    private final TaskManager taskManager;

    String authLink = "";
    private final MutableLiveData<Boolean> shouldHideAuthButton = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> shouldShowAuthView = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> userAuthenticated = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> firstTimeLogin = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public MutableLiveData<Boolean> getShouldHideAuthButton() {
        return shouldHideAuthButton;
    }

    public MutableLiveData<Boolean> getShouldShowAuthView() {
        return shouldShowAuthView;
    }

    public MutableLiveData<Boolean> getUserAuthenticated() {
        return userAuthenticated;
    }

    public MutableLiveData<Boolean> getLoginSuccess() { return loginSuccess; }

    public MutableLiveData<Boolean> isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void notifyToHideOrShowAuthView(boolean visibility) {
        shouldShowAuthView.setValue(visibility);
        shouldHideAuthButton.setValue(visibility);
    }

    public String getAuthLink() {
        return authLink;
    }

    public boolean authLinkAvailable() {
        return !authLink.isEmpty();
    }

    @Inject
    public LoginViewModel(SavedStateHandle stateHandle,
                          KeyRepository keyRepo,
                          ProfileRepository profileRepo,
                          GenreRepository genreRepo,
                          TaskManager taskManager) {
        this.stateHandle = stateHandle;
        this.profileRepo = profileRepo;
        this.keyRepo = keyRepo;
        this.genreRepo = genreRepo;
        this.taskManager = taskManager;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void checkUserActiveSession(String username, String password) {
        String hashedPassword;
        try {
            hashedPassword = (new HashingHelper()).hash(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Log.d("Debug", "username : " + username + " password : " + hashedPassword);
        profileRepo.getUser(username, hashedPassword).subscribe(user -> {
            if(!user.username.isEmpty()) {
                Log.d("Debug", "exits !");
                long now = System.currentTimeMillis();
                Log.d("Debug", "now : " + now + " expired : " + convertTime(user.expiredTime));
                if(now <= 0) { // now >= convertTime(user.expiredTime)
                    Log.d("Debug", "expired !");
                    keyRepo.clearLoginInfoOnFailure();
                    requestToken(username, password);
                    return;
                }
                // thong bao cho user dung session cu vi chua het han
                Log.d("Debug", "satisfied !");
                keyRepo.saveAccountInfo(username);
                profileRepo.cacheUsername(username);
                loginSuccess.setValue(true);
                return;
            }
            // request token de dang nhap
            Log.d("Debug", "Login !");
            requestToken(username, password);
        }, throwable -> {Log.d("Debug", "loi : " + throwable.toString());});
    }

    private long convertTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormat.parse(time);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void requestToken(String username, String password) {
        if(!taskManager.getConnectionMonitor().hasInternetConnection()) {
            error.setValue("No internet connection!");
            return;
        }
        shouldHideAuthButton.setValue(true);
        saveUsername(username);
        savePassword(password);
        keyRepo.requestToken().subscribe(token -> {
            authLink = AppConstant.TMDB_AUTHENTICATE_URL + token;
            shouldShowAuthView.setValue(true);
        }, throwable -> {
            Log.d("Debug", throwable.fillInStackTrace().toString());
        });
    }

    public void notifyUserClickButton() {
        stateHandle.set(USER_CLICK_BUTTON_ALLOW, true);
    }

    public void notifyScreenStop() {
        stateHandle.set(LOGIN_SCREEN_STOP, true);
    }

    public void saveUsername(String username) {
        stateHandle.set(USERNAME, username);
    }

    public void savePassword(String password) {
        stateHandle.set(PASSWORD, password);
    }

    public void checkUserGoForGrantingPermission() {
        boolean existed = stateHandle.contains(USER_CLICK_BUTTON_ALLOW) && stateHandle.contains(LOGIN_SCREEN_STOP);
        boolean finished = Boolean.TRUE.equals(stateHandle.get(USER_CLICK_BUTTON_ALLOW))
                && Boolean.TRUE.equals(stateHandle.get(LOGIN_SCREEN_STOP));
        if(existed && finished) {
            userAuthenticated.setValue(true);
        }
    }

    /** @noinspection ResultOfMethodCallIgnored */
    @SuppressLint("CheckResult")
    public void checkLoginFirstTime() {
        genreRepo.requestUserGenres(profileRepo.getUsername())
                .subscribe(documentSnapshotTask -> {
                    documentSnapshotTask.addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()) {
                                ListProcessingHelper helper = new ListProcessingHelper();
                                List<Genre> userGenres = helper.transformRawListToGenresList(documentSnapshot, genreRepo.getAppGenres());
                                genreRepo.cacheUserGenres(userGenres);
                                firstTimeLogin.setValue(userGenres.isEmpty());
                                return;
                            }
                            firstTimeLogin.setValue(true);
                        }
                        // handle loi.
                    });
                });
    }

    private void saveAccountInfoIfSuccess(String username, String password, String expiredTime) {
        try {
            String hashedPassword = (new HashingHelper()).hash(password);
            profileRepo.insertUser(new User(username, hashedPassword, expiredTime));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void login() {
        String username = stateHandle.get(USERNAME);
        String password = stateHandle.get(PASSWORD);
        keyRepo.requestSessionId(username, password).subscribe(expiredTime -> {
            if(!expiredTime.isEmpty()) {
                shouldShowAuthView.setValue(false);
                keyRepo.saveAccountInfo(username);
                profileRepo.cacheUsername(username);
                saveAccountInfoIfSuccess(username, password, expiredTime);
                loginSuccess.setValue(true);
                return;
            }
            keyRepo.clearLoginInfoOnFailure();
            authLink = "";
            notifyToHideOrShowAuthView(false);
            error.setValue("Username or password is incorrect! Or Time is out");
            loginSuccess.setValue(false);
        }, throwable -> {
        });
    }
}