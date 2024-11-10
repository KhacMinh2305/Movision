package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import architecture.data.local.entity.SearchQuery;
import architecture.data.model.movie.in_app.MovieItem;
import architecture.data.model.people.PeopleItem;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.PeopleRepository;
import architecture.data.repo.ProfileRepository;
import architecture.data.repo.QueryRepository;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import kotlinx.coroutines.CoroutineScope;

@HiltViewModel
public class DiscoverViewModel extends ViewModel {
    private final ProfileRepository profileRepo;
    private final MovieRepository movieRepo;
    private final PeopleRepository peopleRepo;
    private final QueryRepository queryRepo;
    private boolean initialized = false;
    private CompositeDisposable compositeDisposable;
    private final CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);

    private MutableLiveData<List<SearchQuery>> historyQueryState;
    private MutableLiveData<Boolean> historyEmptyTextState;
    private MutableLiveData<Boolean> loadingState;
    private MutableLiveData<Boolean> searchResultAvailableState;
    private MutableLiveData<Boolean> searchResultViewsState;
    private PublishSubject<String> movieQueryEmitter;
    private PublishSubject<String> peopleQueryEmitter;
    private Flowable<PagingData<MovieItem>> movieStream;
    private Flowable<PagingData<PeopleItem>> peopleStream;
    private MutableLiveData<Flowable<PagingData<MovieItem>>> movieStreamState;
    private MutableLiveData<Flowable<PagingData<PeopleItem>>> peopleStreamState;

    public MutableLiveData<Boolean> getHistoryEmptyTextState() { return historyEmptyTextState; }
    public MutableLiveData<List<SearchQuery>> getHistoryQueryState() { return historyQueryState; }
    public MutableLiveData<Boolean> getSearchResultViewsState() { return searchResultViewsState; }
    public MutableLiveData<Boolean> getSearchResultAvailableState() { return searchResultAvailableState; }
    public MutableLiveData<Boolean> getLoadingState() { return loadingState; }
    public MutableLiveData<Flowable<PagingData<MovieItem>>> getMovieStreamState() { return movieStreamState; }
    public MutableLiveData<Flowable<PagingData<PeopleItem>>> getPeopleStreamState() { return peopleStreamState; }

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
        historyEmptyTextState = new MutableLiveData<>(false);
        historyQueryState = new MutableLiveData<>();
        searchResultViewsState = new MutableLiveData<>(true);
        searchResultAvailableState = new MutableLiveData<>(false);
        loadingState = new MutableLiveData<>();
        movieQueryEmitter = PublishSubject.create();
        peopleQueryEmitter = PublishSubject.create();
        movieStreamState = new MutableLiveData<>();
        peopleStreamState = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        observeQueryChanged();
        loadHistoryQueries();
        initialized = true;
    }

    public void onQueryChanged(String query, String tag) {
        if(query == null || query.isEmpty()) return;
        searchResultViewsState.setValue(AppConstant.SEARCH_MOVIE_TAG.equals(tag));
        loadingState.setValue(true);
        if(AppConstant.SEARCH_MOVIE_TAG.equals(tag)) {
            movieQueryEmitter.onNext(query);
            return;
        }
        peopleQueryEmitter.onNext(query);
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void observeQueryChanged() {
        movieQueryEmitter.doOnSubscribe(compositeDisposable::add)
                .debounce(3, TimeUnit.SECONDS).subscribe(query -> {
                    Pager<Integer, MovieItem> pager = movieRepo.getMovieSearchPager(query);
                    movieStream = PagingRx.getFlowable(pager);
                    PagingRx.cachedIn(movieStream, viewModelScope);
                    movieStreamState.postValue(movieStream);
                    searchResultAvailableState.postValue(true);
                    loadingState.postValue(false);
        });
        peopleQueryEmitter.doOnSubscribe(compositeDisposable::add)
                .debounce(3, TimeUnit.SECONDS).subscribe(query -> {
                    Pager<Integer, PeopleItem> pager = peopleRepo.getPeopleSearchPager(query);
                    peopleStream = PagingRx.getFlowable(pager);
                    PagingRx.cachedIn(peopleStream, viewModelScope);
                    peopleStreamState.postValue(peopleStream);
                    loadingState.postValue(false);
                });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadHistoryQueries() {
        queryRepo.getSearchQueriesHistory().subscribe(searchQueries -> {
            if(searchQueries.isEmpty()) {
                historyEmptyTextState.setValue(true);
                return;
            }
            historyQueryState.setValue(new ArrayList<>(searchQueries));
        }, throwable -> Log.d("Debug", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void addSearchQuery(String query) {
        queryRepo.addSearchQuery(query).subscribe(searchQuery ->
                loadHistoryQueries(), throwable -> Log.d("Debug", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void deleteSearchQuery(long id) {
        queryRepo.deleteSearchQuery(id).subscribe(this::loadHistoryQueries);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void clearSearchHistory() {
        queryRepo.clearSearchHistory().subscribe(() ->
                historyQueryState.setValue(new ArrayList<>()));
    }
}
