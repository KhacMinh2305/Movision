package architecture.ui.viewmodel;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import java.time.LocalDateTime;
import java.util.Map;
import javax.inject.Inject;
import architecture.data.model.movie.in_app.DiscoverMovieItem;
import architecture.data.repo.MovieRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;

@HiltViewModel
public class DiscoverResultViewModel extends ViewModel {
    private final MovieRepository movieRepo;
    private Flowable<PagingData<DiscoverMovieItem>> data;
    private boolean initialized = false;

    public Flowable<PagingData<DiscoverMovieItem>> getData() { return data; }

    @Inject
    public DiscoverResultViewModel(MovieRepository movieRepo) {
        this.movieRepo = movieRepo;
    }

    public void init(Float minRate, Float maxRate, Integer minVoteCount, Integer maxVoteCount, String genresId, Integer year) {
        if(initialized) return;
        data = PagingRx.getFlowable(movieRepo.getDiscoverMoviePager(minRate, maxRate, minVoteCount, maxVoteCount, genresId, year));
        PagingRx.cachedIn(data, ViewModelKt.getViewModelScope(this));
        initialized = true;
    }
}
