package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import java.util.Objects;
import javax.inject.Inject;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.GenreRepository;
import architecture.domain.AuthenticationHelper;
import architecture.other.AppMessage;
import architecture.other.ConnectionMonitor;
import architecture.ui.state.VerificationCodeUiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final SavedStateHandle stateHandle;
    private final GenreRepository genreRepo;
    private final AuthenticationRepository authRepo;
    private VerificationCodeUiState sheetUiState;
    private CompositeDisposable compositeDisposable;
    private String gmail;
    private final ConnectionMonitor connMonitor;
    private boolean initialization = false;

    // state fields
    private final MutableLiveData<Boolean> sendingState = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> focusCodeEditTextState = new MutableLiveData<>();
    private final MutableLiveData<String> messageState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genreNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> homeNavigatingState = new MutableLiveData<>();
    public MutableLiveData<Boolean> getSendingState() { return sendingState; }

    /*********************************************************************GETTERS AND SETTERS*********************************************************************/
    // getters
    public MutableLiveData<Boolean> getFocusCodeEditTextState() { return focusCodeEditTextState; }
    public MutableLiveData<String> getMessageState() { return messageState; }
    public VerificationCodeUiState getSheetUiState() { return sheetUiState; }
    public MutableLiveData<Boolean> getGenreNavigationState() { return genreNavigationState; }
    public MutableLiveData<Boolean> getHomeNavigatingState() { return homeNavigatingState; }

    // setters
    public void setSendingState(boolean state) { sendingState.setValue(state); }

    /*********************************************************************CONSTRUCTOR*********************************************************************/
    @Inject
    public LoginViewModel(SavedStateHandle stateHandle, GenreRepository genreRepo,
                          AuthenticationRepository authRepo, ConnectionMonitor connMonitor) {
        this.stateHandle = stateHandle;
        this.genreRepo = genreRepo;
        this.authRepo = authRepo;
        this.connMonitor = connMonitor;
    }

    public void init() {
        if(initialization) return;
        sheetUiState = new VerificationCodeUiState();
        compositeDisposable = new CompositeDisposable();
        initialization = true;
    }

    /*********************************************************************BUSINESSES*********************************************************************/
    public void signInWithEmailAndPassword(String gmail, String password) {
        if(!AuthenticationHelper.checkEmailFormat(gmail)) {
            messageState.setValue(AppMessage.EMAIL_FORMAT_INVALID);
            return;
        }
        if(!AuthenticationHelper.checkPasswordFormat(password)) {
            messageState.setValue(AppMessage.PASSWORD_FORMAT_INVALID);
            return;
        }
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        authRepo.signInUserWithGmailAndPassword(gmail, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                authRepo.updateUser();
                checkUserPeekGenres();
                return;
            }
            messageState.setValue(Objects.requireNonNull(task.getException()).getMessage());
        });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void signInWithGoogle(Context activityContext) {
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        authRepo.loginWithGoogle(activityContext).doOnSubscribe(compositeDisposable::add)
        .subscribe(success -> {
            if(success) {
                checkUserPeekGenres();
            }
            compositeDisposable.dispose();
        }, throwable -> {
            messageState.setValue(throwable.getMessage());
            compositeDisposable.dispose();
        });
    }

    public void getResultFromFacebookTokenRequest(int requestCode, int resultCode, @Nullable Intent data) {
        authRepo.getResultFromFacebookTokenRequest(requestCode, resultCode, data);
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void signInWithFacebook(Fragment fragment) {
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        authRepo.getFacebookLoginState(fragment).doOnSubscribe(compositeDisposable::add)
                .subscribe(result -> {
                    compositeDisposable.dispose();
                    if(result.equals(AppMessage.SIGN_IN_FB_RESULT_SUCCESS)) {
                        checkUserPeekGenres();
                        return;
                    }
                    messageState.setValue(result);
                }, throwable -> {
                    messageState.setValue(throwable.getMessage());
                    compositeDisposable.dispose();
                });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void sendCodeForUpdatingPassword(String gmail) {
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        if(!AuthenticationHelper.checkEmailFormat(gmail)) {
            messageState.setValue(AppMessage.EMAIL_FORMAT_INVALID);
            return;
        }
        if(sheetUiState.isCountingDown) {
            messageState.setValue(AppMessage.REQUEST_CODE_TIME_NOT_OVER);
            sheetUiState.setSheetState(true);
            sheetUiState.setLoadingState(false);
            return;
        }
        sendingState.setValue(true);
        this.gmail = gmail;
        authRepo.sendVerificationCodeToEmail(gmail)
                .doOnSubscribe(disposable -> compositeDisposable.add(disposable))
                .subscribe(sent -> {
                    sheetUiState.setLoadingState(false);
                    sheetUiState.setSheetState(true);
                    sheetUiState.setEmailState(gmail);
                    sheetUiState.countDown();
                }, throwable -> messageState.setValue(throwable.getMessage()));
    }

    public void sendUserResetPassGmail() {
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        sheetUiState.setLoadingState(true);
        authRepo.sendUserResetPasswordEmail(gmail).addOnCompleteListener(task -> {
            sheetUiState.setLoadingState(false);
            if(task.isSuccessful()) {
                messageState.setValue(AppMessage.RESET_PASS_EMAIL);
                return;
            }
            Exception exception = task.getException();
            if(exception != null) {
                messageState.setValue(exception.getMessage());
            }
        });
    }

    public void verifyCode(String code) {
        if(authRepo.checkCode(code)) {
            sheetUiState.isCountingDown = false;
            sendUserResetPassGmail();
            return;
        }
        sheetUiState.clearCode();
        messageState.setValue(AppMessage.CODE_IS_NOT_VALID);
        focusCodeEditTextState.setValue(true);
    }

    public void sendCodeAgain() {
        if(!connMonitor.hasInternetConnection()) {
            messageState.setValue(AppMessage.NOT_CONNECTION);
            return;
        }
        sheetUiState.setLoadingState(true);
        sendCodeForUpdatingPassword(this.gmail);
    }

    @SuppressLint("CheckResult")
    private void checkUserPeekGenres() {
        genreRepo.requestUserGenres(authRepo.getUserUid()).addOnSuccessListener(genres -> {
            if(!genres.isEmpty()) {
                homeNavigatingState.setValue(true);
                return;
            }
            genreNavigationState.setValue(true);
        }).addOnFailureListener(e -> messageState.setValue(e.getMessage()));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}