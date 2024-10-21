package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import javax.inject.Inject;
import architecture.data.repo.MediaRepository;
import architecture.data.repo.ProfileRepository;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@HiltViewModel
public class ChangeAvatarViewModel extends ViewModel {

    private final MediaRepository mediaRepo;
    private final ProfileRepository profileRepo;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<byte[]> navigateState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>(false);

    public MutableLiveData<byte[]> getNavigateState() { return navigateState; }
    public MutableLiveData<Boolean> getLoadingState() { return loadingState; }

    @Inject
    public ChangeAvatarViewModel(MediaRepository mediaRepo, ProfileRepository profileRepo) {
        this.mediaRepo = mediaRepo;
        this.profileRepo = profileRepo;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void changeUserAvatar(byte[] image) {
        loadingState.setValue(true);
        mediaRepo.pushUserAvatarToStorage(profileRepo.getUserUid(), image)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe(result -> {
                    if(result.containsKey(AppConstant.SUCCESS_CODE)) {
                        String avatarUrl = result.get(AppConstant.SUCCESS_CODE);
                        profileRepo.updateUserProfile(null, avatarUrl).subscribe(success -> {
                            loadingState.setValue(false);
                            navigateState.setValue(image);
                            compositeDisposable.dispose();
                            compositeDisposable.clear();
                        });
                    }
                }, throwable -> Log.d("ERROR", throwable.toString()));
    }
}
