package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import javax.inject.Inject;
import architecture.data.repo.MediaRepository;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.PeopleRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PeopleViewModel extends ViewModel {

    private final PeopleRepository peopleRepo;
    private final MediaRepository mediaRepo;
    private final MovieRepository movieRepo; // add "Casting movie" List
    private int personId;
    private boolean initialized = false;

    private MutableLiveData<String> personPosterPathState;
    private MutableLiveData<String> personNameState;
    private MutableLiveData<String> personBioState;
    private MutableLiveData<String> personBirthDayState;
    private MutableLiveData<String> personPositionState;
    private MutableLiveData<String> personBirthPlaceState;
    private MutableLiveData<List<String>> imagesAlbumState;

    public MutableLiveData<String> getPersonPosterPathState() { return personPosterPathState; }
    public MutableLiveData<String> getPersonNameState() { return personNameState; }
    public MutableLiveData<String> getPersonBioState() { return personBioState; }
    public MutableLiveData<String> getPersonBirthDayState() { return personBirthDayState; }
    public MutableLiveData<String> getPersonPositionState() { return personPositionState; }
    public MutableLiveData<String> getPersonBirthPlaceState() { return personBirthPlaceState; }
    public MutableLiveData<List<String>> getImagesAlbumState() { return imagesAlbumState; }

    @Inject
    public PeopleViewModel(PeopleRepository peopleRepo, MediaRepository mediaRepo, MovieRepository movieRepo) {
        this.peopleRepo = peopleRepo;
        this.mediaRepo = mediaRepo;
        this.movieRepo = movieRepo;
    }

    public void init(int personId) {
        if (initialized) return;
        this.personId = personId;
        personPosterPathState = new MutableLiveData<>();
        personNameState = new MutableLiveData<>();
        personBioState = new MutableLiveData<>();
        personBirthDayState = new MutableLiveData<>();
        personPositionState = new MutableLiveData<>();
        personBirthPlaceState = new MutableLiveData<>();
        imagesAlbumState = new MutableLiveData<>();
        loadPeople();
        loadPersonImages();
        initialized = true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPeople() {
        peopleRepo.loadPeopleDetails(personId).subscribe(details -> {
            personPosterPathState.postValue(details.posterPath);
            personNameState.postValue(details.name);
            personBioState.postValue(details.biography);
            personBirthDayState.postValue(details.birthDay);
            personPositionState.postValue(details.department);
            personBirthPlaceState.postValue(details.placeOfBirth);
        }, throwable -> Log.d("DEBUG", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPersonImages() {
        mediaRepo.loadPersonImages(personId).subscribe(imagesAlbumState::setValue,
                throwable -> Log.d("DEBUG", throwable.toString()));
    }
}
