package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import javax.inject.Inject;
import architecture.data.model.movie.in_app.MovieReview;
import architecture.data.model.movie.in_app.ReviewAuthor;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.ProfileRepository;
import architecture.domain.HashingHelper;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ReviewViewModel extends ViewModel {

    private final MovieRepository movieRepo;
    private final ProfileRepository profileRepo;
    private int movieId;
    private Flowable<PagingData<MovieReview>> reviewFlowable;
    private boolean initialized = false;

    private MutableLiveData<Boolean> addReviewState;

    public Flowable<PagingData<MovieReview>> getReviewFlowable() { return reviewFlowable; }
    public MutableLiveData<Boolean> getAddReviewState() { return addReviewState; }

    @Inject
    public ReviewViewModel(MovieRepository movieRepo, ProfileRepository profileRepo) {
        this.movieRepo = movieRepo;
        this.profileRepo = profileRepo;
    }

    public void init(int movieId) {
        if(initialized) return;
        this.movieId = movieId;
        reviewFlowable = PagingRx.getFlowable(movieRepo.getMovieReviewPager(movieId));
        PagingRx.cachedIn(reviewFlowable, ViewModelKt.getViewModelScope(this));
        addReviewState = new MutableLiveData<>();
        initialized = true;
    }

    private Single<MovieReview> createReview(String content) {
        HashingHelper hashingHelper = new HashingHelper();
        return Single.fromCallable(() -> {
            long time = System.currentTimeMillis();
            String authorId = profileRepo.getUserUid();
            String authorName = profileRepo.getUserData().get("name");
            authorName = (AppConstant.UNDEFINED_FIELD.equals(authorName)) ? "User#" + authorId : authorName;
            String avatarUrl = profileRepo.getUserAvatarUrl();
            avatarUrl = (avatarUrl == null) ? "" : avatarUrl;
            ReviewAuthor author = new ReviewAuthor(authorId, authorName, avatarUrl);
            String reviewId = hashingHelper.hash(movieId + authorId + time);
            return new MovieReview(reviewId, content, time, author);
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void addMovieReview(String content) {
        if(content == null || content.isEmpty()) return;
        createReview(content).subscribe((review, throwable) -> {
            if(throwable != null) return;
            movieRepo.addMovieReviews(movieId, review).subscribe(() -> addReviewState.setValue(true));
        });
    }
}

//