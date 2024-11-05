package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import javax.inject.Inject;
import architecture.data.local.entity.SearchQuery;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.PeopleRepository;
import architecture.data.repo.ProfileRepository;
import architecture.data.repo.QueryRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DiscoverViewModel extends ViewModel {
    private final ProfileRepository profileRepo;
    private final MovieRepository movieRepo;
    private final PeopleRepository peopleRepo;
    private final QueryRepository queryRepo;
    private boolean initialized = false;

    private MutableLiveData<List<SearchQuery>> historyQueryState;
    private MutableLiveData<Boolean> historyVisibilityState;

    public MutableLiveData<Boolean> getHistoryVisibilityState() { return historyVisibilityState; }
    public MutableLiveData<List<SearchQuery>> getHistoryQueryState() { return historyQueryState; }

    @Inject
    public DiscoverViewModel(ProfileRepository profileRepo, MovieRepository movieRepo,
                             PeopleRepository peopleRepo, QueryRepository queryRepo) {
        this.profileRepo = profileRepo;
        this.movieRepo = movieRepo;
        this.peopleRepo = peopleRepo;
        this.queryRepo = queryRepo;
    }

    public void init() {
        if(initialized) return;
        historyVisibilityState = new MutableLiveData<>();
        historyQueryState = new MutableLiveData<>();
        loadHistoryQueries();
        initialized = true;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadHistoryQueries() {
        queryRepo.getSearchQueriesHistory().subscribe(historyQueryState::setValue,
                throwable -> Log.d("Debug", throwable.toString()));
    }
}
