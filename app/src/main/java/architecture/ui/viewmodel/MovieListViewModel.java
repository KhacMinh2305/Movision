package architecture.ui.viewmodel;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import javax.inject.Inject;
import architecture.data.local.entity.Movie;
import architecture.data.repo.GenreRepository;
import architecture.data.repo.MovieRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;

@HiltViewModel
public class MovieListViewModel extends ViewModel {
    private final int PAGE_SIZE = 15;
    private final SavedStateHandle savedStateHandle;
    private final GenreRepository genreRepo;
    private final MovieRepository movieRepo;
    private Flowable<PagingData<Movie>> movieFlowable;
    private boolean initialized = false;

    public Flowable<PagingData<Movie>> getMovieFlowable() {
        return movieFlowable;
    }

    @Inject
    public MovieListViewModel(SavedStateHandle savedStateHandle, GenreRepository genreRepo, MovieRepository movieRepo) {
        this.savedStateHandle = savedStateHandle;
        this.genreRepo = genreRepo;
        this.movieRepo = movieRepo;
    }

    public void init(String tag) {
        if(!initialized) {
            movieFlowable = PagingRx.getFlowable(movieRepo.getMoviePager(tag, PAGE_SIZE));
            PagingRx.cachedIn(movieFlowable, ViewModelKt.getViewModelScope(this));
            initialized = true;
        }
    }
}

