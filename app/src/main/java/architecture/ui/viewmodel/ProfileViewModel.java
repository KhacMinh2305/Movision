package architecture.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import architecture.data.repo.AuthenticationRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthenticationRepository authRepo;

    private final MutableLiveData<Boolean> signOutNavigationState = new MutableLiveData<>();

    public MutableLiveData<Boolean> getSignOutNavigationState() { return signOutNavigationState; }

    @Inject
    public ProfileViewModel(AuthenticationRepository authRepo) {
        this.authRepo = authRepo;
    }

    public void signOut() {
        authRepo.logout();
        signOutNavigationState.setValue(true);
    }

}
