package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import java.util.Objects;
import javax.inject.Inject;
import architecture.data.repo.AuthenticationRepository;
import architecture.domain.AuthenticationHelper;
import architecture.other.AppMessage;
import architecture.ui.state.VerificationCodeUiState;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel {

    private static final String GMAIL = "gmail";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "confirmPassword";
    private final SavedStateHandle stateHandle;
    private final AuthenticationRepository authRepo;
    private VerificationCodeUiState sheetUiState;

    private final MutableLiveData<Boolean> loadingImageViewVisibility = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigatingState = new MutableLiveData<>();
    private boolean initialized = false;

    // Getters
    public VerificationCodeUiState getSheetUiState() { return sheetUiState; }
    public MutableLiveData<Boolean> getLoadingImageViewVisibility() { return loadingImageViewVisibility; }
    public MutableLiveData<String> getMessage() { return message; }
    public MutableLiveData<Boolean> getNavigatingState() { return navigatingState; }

    @Inject
    public SignUpViewModel(SavedStateHandle stateHandle, AuthenticationRepository authRepo) {
        this.stateHandle = stateHandle;
        this.authRepo = authRepo;
    }

    public void init() {
        if (initialized) {
            return;
        }
        sheetUiState = new VerificationCodeUiState();
        initialized = true;
    }

    private boolean checkFieldsFormat(String email, String password) {
        if(!AuthenticationHelper.checkEmailFormat(email)) {
            message.setValue(AppMessage.EMAIL_FORMAT_INVALID);
            return false;
        }
        if(!AuthenticationHelper.checkPasswordFormat(password)) {
            message.setValue(AppMessage.PASSWORD_FORMAT_INVALID);
            return false;
        }
        return true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void requestVerificationCode(String gmail, String password, String confirmPassword) {
        if(!AuthenticationHelper.checkConfirmPassWordMatch(password, confirmPassword)) {
            message.setValue(AppMessage.CONFIRM_PASSWORD_NOT_MATCH);
            return;
        }
        if(!checkFieldsFormat(gmail, password)) {
            return;
        }
        if(sheetUiState.isCountingDown) {
            message.setValue(AppMessage.REQUEST_CODE_TIME_NOT_OVER);
            sheetUiState.getSheetState().setValue(true);
            return;
        }
        loadingImageViewVisibility.setValue(true);
        authRepo.sendVerificationCodeToEmail(gmail).subscribe(sent -> {
                    loadingImageViewVisibility.setValue(false);
                    sheetUiState.getSheetState().setValue(true);
                    sheetUiState.setEmailState(gmail);
                    sheetUiState.countDown();
                    saveSignUpInfo(gmail, password, confirmPassword);
                },
                throwable -> message.setValue(throwable.getMessage()));
    }

    private void signUp() {
        loadingImageViewVisibility.setValue(true);
        authRepo.signUpWithEmailAndPassword(stateHandle.get(GMAIL), stateHandle.get(PASSWORD)).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                loadingImageViewVisibility.setValue(false);
                authRepo.updateUser();
                navigatingState.setValue(true);
                return;
            }
            message.setValue(Objects.requireNonNull(task.getException()).getMessage());
        });
    }

    public void checkCode(String code) {
        if(authRepo.checkCode(code)) {
            sheetUiState.isCountingDown = false;
            signUp();
            return;
        }
        sheetUiState.getCodeCheckingResult().setValue(false);
        message.setValue(AppMessage.CODE_IS_NOT_VALID);
    }

    public void saveSignUpInfo(String gmail, String password, String confirmPassword) {
        stateHandle.set(GMAIL, gmail);
        stateHandle.set(PASSWORD, password);
        stateHandle.set(CONFIRM_PASSWORD, confirmPassword);
    }

    public void resendCode() {
        if(sheetUiState.isCountingDown) {
            message.setValue(AppMessage.REQUEST_CODE_TIME_NOT_OVER);
        }
        requestVerificationCode(stateHandle.get(GMAIL), stateHandle.get(PASSWORD), stateHandle.get(CONFIRM_PASSWORD));
    }
}
