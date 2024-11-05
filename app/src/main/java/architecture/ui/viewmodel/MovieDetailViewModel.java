package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import architecture.data.model.movie.in_app.ClipUrl;
import architecture.data.model.movie.in_app.SimilarMovie;
import architecture.data.model.people.Caster;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.PeopleRepository;
import architecture.data.repo.ProfileRepository;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovieDetailViewModel extends ViewModel {

    private final MovieRepository movieRepo;
    private final PeopleRepository peopleRepo;
    private final ProfileRepository profileRepo;
    private boolean initialized = false;
    private int movieId;

    private MutableLiveData<String> movieImageUrlState;
    private MutableLiveData<String> movieNameState;
    private MutableLiveData<String> movieGenresState;
    private MutableLiveData<String> movieDescriptionState;
    private MutableLiveData<Integer> movieDurationState;
    private MutableLiveData<String> movieYearState;
    private MutableLiveData<String> movieRatingState;
    private MutableLiveData<Boolean> ratingButtonVisibility;
    private MutableLiveData<Boolean> ratingTextViewVisibilityState;
    private MutableLiveData<String> ratingTextViewTextState;
    private MutableLiveData<Boolean> favoriteState;
    private MutableLiveData<List<Caster>> castersState;
    private MutableLiveData<List<ClipUrl>> movieClipsState;
    private MutableLiveData<List<SimilarMovie>> similarMovieState;
    private MutableLiveData<String> messageState;

    public MutableLiveData<String> getMovieImageUrlState() { return movieImageUrlState; }
    public MutableLiveData<String> getMovieNameState() { return movieNameState; }
    public MutableLiveData<String> getMovieGenresState() { return movieGenresState; }
    public MutableLiveData<String> getMovieDescriptionState() { return movieDescriptionState; }
    public MutableLiveData<Integer> getMovieDurationState() { return movieDurationState; }
    public MutableLiveData<String> getMovieYearState() { return movieYearState; }
    public MutableLiveData<String> getMovieRatingState() { return movieRatingState; }
    public MutableLiveData<Boolean> getRatingButtonVisibility() { return ratingButtonVisibility; }
    public MutableLiveData<Boolean> getRatingTextViewVisibilityState() { return ratingTextViewVisibilityState; }
    public MutableLiveData<String> getRatingTextViewTextState() { return ratingTextViewTextState; }
    public MutableLiveData<Boolean> getFavoriteState() { return favoriteState; }
    public MutableLiveData<List<Caster>> getCastersState() { return castersState; }
    public MutableLiveData<List<ClipUrl>> getMovieClipsState() { return movieClipsState; }
    public MutableLiveData<List<SimilarMovie>> getSimilarMovieState() { return similarMovieState; }
    public MutableLiveData<String> getMessageState() { return messageState; }

    @Inject
    public MovieDetailViewModel(MovieRepository movieRepo, PeopleRepository peopleRepo, ProfileRepository profileRepo) {
        this.movieRepo = movieRepo;
        this.peopleRepo = peopleRepo;
        this.profileRepo = profileRepo;
    }

    public void init(int movieId) {
        this.movieId = movieId;
        if(initialized) return;
        initStates();
        loadInitially();
        initialized = true;
    }

    private void initStates() {
        movieImageUrlState = new MutableLiveData<>();
        movieNameState = new MutableLiveData<>();
        movieGenresState = new MutableLiveData<>();
        movieDescriptionState = new MutableLiveData<>();
        movieDurationState = new MutableLiveData<>();
        movieYearState = new MutableLiveData<>();
        movieRatingState = new MutableLiveData<>();
        ratingButtonVisibility = new MutableLiveData<>(true);
        ratingTextViewVisibilityState = new MutableLiveData<>(false);
        ratingTextViewTextState = new MutableLiveData<>();
        favoriteState = new MutableLiveData<>();
        castersState = new MutableLiveData<>();
        movieClipsState = new MutableLiveData<>();
        similarMovieState = new MutableLiveData<>();
        messageState = new MutableLiveData<>();
    }

    private void loadInitially() {
        loadMovieDetails();
        loadMovieCasters();
        loadMovieClips();
        loadSimilarMovies();
        getRatingOfMovie();
        checkMovieFavorite();
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadMovieDetails() {
        movieRepo.getMovieDetails(movieId).subscribe(details -> {
            movieImageUrlState.setValue(AppConstant.TMDB_IMAGE_HOST + details.posterPath);
            movieNameState.setValue(details.name);
            movieGenresState.setValue(details.genres);
            movieDescriptionState.setValue(details.overview);
            movieDurationState.setValue(details.duration);
            movieYearState.setValue(String.valueOf(details.year));
            movieRatingState.setValue(String.valueOf(details.rating));
        }, throwable -> Log.d("ERROR", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadMovieCasters() {
        peopleRepo.loadMovieCasters(movieId).subscribe(castersState::setValue,
                throwable -> Log.d("ERROR_CASTERS", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadMovieClips() {
        movieRepo.getMovieClips(movieId).subscribe(movieClipsState::setValue,
                throwable -> Log.d("ERROR_CLIPS", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadSimilarMovies() {
        movieRepo.loadSimilarPeople(movieId, 1)
                .subscribe(similarMovies -> similarMovieState.setValue(similarMovies),
                        throwable -> Log.d("ERROR_SIMILAR", throwable.toString()));
    }

    public void getRatingOfMovie() {
        movieRepo.getRatingOfMovie(profileRepo.getUserUid(), movieId).addOnSuccessListener(rating -> {
            if(rating != - 1) {
                ratingButtonVisibility.setValue(false);
                ratingTextViewVisibilityState.setValue(true);
                ratingTextViewTextState.setValue(String.valueOf(rating));
            }
        }).addOnFailureListener(e -> Log.d("ERROR_CHECK", e.toString()));
    }

    public void checkMovieFavorite() {
        movieRepo.checkMovieFavorite(profileRepo.getUserUid(), movieId)
                .addOnSuccessListener(favoriteState::setValue)
                .addOnFailureListener(e -> Log.d("Debug", e.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void rateMovie(float rating) {
        String movieName = movieNameState.getValue();
        double movieRating = Double.parseDouble(Objects.requireNonNull(movieRatingState.getValue()));
        String posterPath = movieImageUrlState.getValue();
        movieRepo.rateMovie(profileRepo.getUserUid(), movieId, movieName, movieRating, rating, posterPath).subscribe(result -> {
            ratingButtonVisibility.setValue(false);
            ratingTextViewVisibilityState.setValue(true);
            ratingTextViewTextState.setValue(String.valueOf(rating));
        }, throwable -> Log.d("DEBUG_CHECK", throwable.toString()));
    }

    public void addToFavoriteList() {
        if(Boolean.TRUE.equals(favoriteState.getValue())) return;
        Log.d("Debug", "Them vao danh sach yeu thich !");
        String movieName = movieNameState.getValue();
        double movieRating = Double.parseDouble(Objects.requireNonNull(movieRatingState.getValue()));
        String posterPath = movieImageUrlState.getValue();
        movieRepo.addToFavoriteList(profileRepo.getUserUid(), movieId, movieName, movieRating, posterPath)
                .addOnSuccessListener(unused -> Log.d("Debug", "Success"))
                .addOnFailureListener(e -> Log.d("Debug", e.toString()));
    }

    public void removeFromFavoriteList() {
        movieRepo.removeFromFavoriteList(profileRepo.getUserUid(), movieId)
                .addOnSuccessListener(unused -> Log.d("Debug", "Success"))
                .addOnFailureListener(e -> Log.d("Debug", e.toString()));
    }
}
