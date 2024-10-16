package architecture.ui.viewmodel;
import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import javax.inject.Inject;
import architecture.data.local.entity.People;
import architecture.data.repo.PeopleRepository;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlinx.coroutines.CoroutineScope;

@HiltViewModel
public class PeopleListViewModel extends ViewModel {
    private final PeopleRepository peopleRepo;
    private final BehaviorSubject<Integer> filterSubject = BehaviorSubject.createDefault(AppConstant.HUMAN_ALL);
    private Flowable<PagingData<People>> peopleFlowable;
    private MutableLiveData<Flowable<PagingData<People>>> peopleLiveData = new MutableLiveData<>();
    private final CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
    private boolean initialized = false;

    /*public Flowable<PagingData<People>> getPeopleFlowable() {
        return peopleFlowable;
    }*/

    public MutableLiveData<Flowable<PagingData<People>>> getPeopleLiveData() {
        return peopleLiveData;
    }

    @Inject
    public PeopleListViewModel(PeopleRepository peopleRepo) {
        this.peopleRepo = peopleRepo;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void init() {
        if(!initialized) {
            filterSubject.subscribe(gender -> {
                Pager<Integer, People> pager = peopleRepo.getPeoplePager(gender);
                peopleFlowable = PagingRx.getFlowable(pager);
                PagingRx.cachedIn(peopleFlowable, viewModelScope);
                peopleLiveData.setValue(peopleFlowable);
            });
            initialized = true;
        }
    }

    public void applyFilter(int gender) {
        filterSubject.onNext(gender);
    }
}
