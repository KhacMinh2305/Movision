package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Map;
import javax.inject.Inject;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.ProfileRepository;
import architecture.domain.AuthenticationHelper;
import architecture.other.AppMessage;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final AuthenticationRepository authRepo;
    private final ProfileRepository profileRepo;
    private boolean initialized = false;

    private final MutableLiveData<String> userAvatarState = new MutableLiveData<>();
    private final MutableLiveData<String> usernameState = new MutableLiveData<>();
    public MutableLiveData<String> userGmailState = new MutableLiveData<>();
    public MutableLiveData<String> numberPhoneState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signOutNavigationState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sheetState = new MutableLiveData<>();
    private final MutableLiveData<String> messageState = new MutableLiveData<>();

    public MutableLiveData<String> getUserAvatarState() { return userAvatarState; }
    public MutableLiveData<String> getUsernameState() { return usernameState; }
    public MutableLiveData<String> getUserGmailState() { return userGmailState; }
    public MutableLiveData<String> getNumberPhoneState() { return numberPhoneState; }
    public MutableLiveData<Boolean> getSignOutNavigationState() { return signOutNavigationState; }
    public MutableLiveData<Boolean> getSheetState() { return sheetState; }
    public MutableLiveData<String> getMessageState() { return messageState; }

    public void setSheetState(boolean state) { sheetState.setValue(state); }

    @Inject
    public ProfileViewModel(AuthenticationRepository authRepo, ProfileRepository profileRepo) {
        this.authRepo = authRepo;
        this.profileRepo = profileRepo;
    }

    public void init() {
        if(initialized) return;
        String userAvatarUrl = profileRepo.getUserAvatarUrl();
        userAvatarState.setValue(userAvatarUrl);
        Map<String, String> userInfo = profileRepo.getUserData();
        usernameState.setValue(userInfo.get("name"));
        userGmailState.setValue(userInfo.get("gmail"));
        numberPhoneState.setValue(userInfo.get("phoneNumber"));
        initialized = true;
    }

    private boolean checkPasswordValidity(String oldPassword, String newPassword) {
        if(oldPassword == null || oldPassword.isEmpty()) {
            messageState.setValue(AppMessage.PASSWORD_EMPTY);
            return false;
        }
        if(!AuthenticationHelper.checkPasswordFormat(newPassword)) {
            messageState.setValue(AppMessage.PASSWORD_FORMAT_INVALID);
            return false;
        }
        return true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void changePassword(String oldPassword, String newPassword) {
        if(!checkPasswordValidity(oldPassword, newPassword)) {
            return;
        }
        authRepo.changePassword(oldPassword, newPassword).subscribe(result -> {
            if(result) {
                sheetState.setValue(false);
                messageState.setValue(AppMessage.PASSWORD_CHANGE_SUCCESS);
                return;
            }
            sheetState.setValue(false);
            messageState.setValue(AppMessage.PASSWORD_CHANGE_FAILED);
        }, throwable -> Log.d("Debug", throwable.toString()));
    }

    public void signOut() {
        authRepo.logout();
        signOutNavigationState.setValue(true);
    }
}
