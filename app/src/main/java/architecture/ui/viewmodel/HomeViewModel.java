package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import architecture.data.local.entity.Movie;
import architecture.data.model.genre.Genre;
import architecture.data.repo.GenreRepository;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.ProfileRepository;
import architecture.other.AppConstant;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    //-------------------------------------------------------FIELDS-------------------------------------------------------
    // Repo, Injected Items and so on..
    private final SavedStateHandle stateHandle;
    private final ProfileRepository profileRepo;
    private final MovieRepository movieRepo;
    private final GenreRepository genreRepo;
    private boolean loadingSignal = false;

    // genres
    private final MutableLiveData<List<String>> listUpcomingMoviePosterUrls = new MutableLiveData<>();
    private final MutableLiveData<List<Genre>> listUserGenres = new MutableLiveData<>();
    private final MutableLiveData<List<Genre>> listGenresToPeek = new MutableLiveData<>();
    private final MutableLiveData<List<Genre>> userGenresTemp = new MutableLiveData<>();
    private final List<Genre> userPeekGenres = new ArrayList<>();
    private final List<Genre> genresToPeek = new ArrayList<>();

    // Category
    private final MutableLiveData<List<Movie>> previewTrendingMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> previewTopRatedMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> previewPopularMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> previewPlayingMovies = new MutableLiveData<>();


    //-------------------------------------------------------GETTERS-------------------------------------------------------
    // Genres
    public MutableLiveData<List<Genre>> getListUserGenres() {
        return listUserGenres;
    }
    public MutableLiveData<List<Genre>> getListGenresToPeek() {
        return listGenresToPeek;
    }
    public MutableLiveData<List<Genre>> getUserGenresTemp() {
        return userGenresTemp;
    }

    // Preview (RecyclerView Movie)
    public MutableLiveData<List<String>> getListUpcomingMoviePosterUrls() { return listUpcomingMoviePosterUrls; }
    public MutableLiveData<List<Movie>> getPreviewPlayingMovies() { return previewPlayingMovies; }
    public MutableLiveData<List<Movie>> getPreviewPopularMovies() { return previewPopularMovies; }
    public MutableLiveData<List<Movie>> getPreviewTopRatedMovies() { return previewTopRatedMovies; }
    public MutableLiveData<List<Movie>> getPreviewTrendingMovies() { return previewTrendingMovies; }



    //-------------------------------------------------------SETTERS-------------------------------------------------------
    public void resetLoading() {
        loadingSignal = false;
    }


    //-------------------------------------------------------CONSTRUCTORS + INITIALIZATION-------------------------------------------------------
    @Inject
    public HomeViewModel(SavedStateHandle stateHandle, ProfileRepository profileRepo,
                         MovieRepository movieRepo, GenreRepository genreRepo) {
        this.stateHandle = stateHandle;
        this.profileRepo = profileRepo;
        this.movieRepo = movieRepo;
        this.genreRepo = genreRepo;
    }

    public void loadInit() {
        if(!loadingSignal) {
            loadUpComingMovie();
            loadUserGenres();
            loadTrendingMovies();
            loadTopRatedMovies();
            loadPopularMovies();
            loadPlayingMovies();
            loadingSignal = true;
        }
    }

    //-------------------------------------------------------Businesses-------------------------------------------------------

    // --------------------------Genres--------------------------
    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadUserGenres() {
        Single.fromCallable(() -> {
                    List<Genre> userGenres = genreRepo.getUserGenres();
                    userPeekGenres.addAll(userGenres);
                    List<Genre> appGenres = genreRepo.getAppGenres();
                    for(Genre genre : appGenres) {
                        if(!userPeekGenres.contains(genre)) {
                            genresToPeek.add(genre);
                        }
                    }
                    return userGenres;
                }).subscribeOn(Schedulers.single())
                .map(genres -> (List<Genre>) new ArrayList<>(genres))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(genres -> {
                    listUserGenres.setValue(genres);
                    updateTempGenresLists();
                });
    }

    @SuppressLint("CheckResult")
    private void updateTempGenresLists() {
        Completable.fromAction(() -> {
            userGenresTemp.postValue(new ArrayList<>(userPeekGenres));
            listGenresToPeek.postValue(new ArrayList<>(genresToPeek));
        }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single()).subscribe();
    }

    public void updateUserGenres() {
        genreRepo.updateUserGenres(profileRepo.getUsername(), userPeekGenres).addOnSuccessListener(runnable -> {
            genreRepo.cacheUserGenres(userPeekGenres);
            listUserGenres.setValue(genreRepo.getUserGenres());
        }).addOnFailureListener(runnable -> {
            // handle error
        });
    }

    public void addGenreFromUserList(Genre genre) {
        userPeekGenres.add(genre);
        genresToPeek.remove(genre);
        updateTempGenresLists();
    }

    public void removeGenreFromUserList(Genre genre) {
        userPeekGenres.remove(genre);
        genresToPeek.add(genre);
        updateTempGenresLists();
    }

    // ------------------Preview Data For RecyclerViews------------------
    @SuppressLint("CheckResult")
    private void loadUpComingMovie() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        listUpcomingMoviePosterUrls.setValue(imageUrls);
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadTrendingMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_TRENDING_TITLE).subscribe((movies, throwable) -> {
            previewTrendingMovies.setValue(movies);
        });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadTopRatedMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_TOP_RATED_TITLE).subscribe((movies, throwable) -> {
            previewTopRatedMovies.setValue(movies);
        });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPopularMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_POPULAR_TITLE).subscribe((movies, throwable) -> {
            previewPopularMovies.setValue(movies);
        });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPlayingMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_PLAYING_TITLE).subscribe((movies, throwable) -> {
            previewPlayingMovies.setValue(movies);
        });
    }
}
