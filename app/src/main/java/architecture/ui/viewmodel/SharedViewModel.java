package architecture.ui.viewmodel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import architecture.data.repo.KeyRepository;
import architecture.other.ConnectionMonitor;
import architecture.other.TaskManager;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SharedViewModel extends ViewModel {

    private final KeyRepository profileRepo;
    private final TaskManager taskManager;
    private final MutableLiveData<Boolean> shouldHideBottomNavBar = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> loadingHomeDataSignal = new MutableLiveData<>();

    // getters
    public MutableLiveData<Boolean> getShouldHideBottomNavBar() {return shouldHideBottomNavBar;}
    public ConnectionMonitor getConnectionMonitor() { return taskManager.getConnectionMonitor(); }
    public MutableLiveData<Boolean> getLoadingHomeDataSignal() { return loadingHomeDataSignal; }

    // setters
    public void setBottomNavBarVisibility(boolean visibility) {
        shouldHideBottomNavBar.setValue(!visibility);
    }

    public void setLoadingHomeDataState(boolean state) {
        loadingHomeDataSignal.setValue(state);
    }

    @Inject
    public SharedViewModel(KeyRepository profileRepo, TaskManager taskManager) {
        this.profileRepo = profileRepo;
        this.taskManager = taskManager;
    }
}
