package architecture.ui.viewmodel;
import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.People;
import architecture.data.model.genre.Genre;
import architecture.data.repo.AuthenticationRepository;
import architecture.data.repo.GenreRepository;
import architecture.data.repo.MovieRepository;
import architecture.data.repo.PeopleRepository;
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
    private final AuthenticationRepository authRepo;
    private final ProfileRepository profileRepo;
    private final MovieRepository movieRepo;
    private final GenreRepository genreRepo;
    private final PeopleRepository peopleRepo;
    private boolean loadingSignal = false;

    private final MutableLiveData<String> userAvatarState = new MutableLiveData<>();

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

    // People
    private final MutableLiveData<List<People>> previewPopularPeople = new MutableLiveData<>();
    private final MutableLiveData<List<People>> previewTrendingPeople = new MutableLiveData<>();

    // Personal fields
    private final MutableLiveData<Genre> personalGenre = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> personalMovies = new MutableLiveData<>();

    //-------------------------------------------------------GETTERS-------------------------------------------------------
    public MutableLiveData<String> getUserAvatarState() { return userAvatarState; }

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

    // People
    public MutableLiveData<List<People>> getPreviewPopularPeople() { return previewPopularPeople; }
    public MutableLiveData<List<People>> getPreviewTrendingPeople() { return previewTrendingPeople; }

    // Personal
    public MutableLiveData<Genre> getPersonalGenre() { return personalGenre; }
    public MutableLiveData<List<Movie>> getPersonalMovies() { return personalMovies; }

    //-------------------------------------------------------SETTERS-------------------------------------------------------
    public void resetLoading() { loadingSignal = false; }


    //-------------------------------------------------------CONSTRUCTORS + INITIALIZATION-------------------------------------------------------
    @Inject
    public HomeViewModel(SavedStateHandle stateHandle, AuthenticationRepository authRepo,
                         ProfileRepository profileRepo, MovieRepository movieRepo,
                         GenreRepository genreRepo, PeopleRepository peopleRepo) {
        this.stateHandle = stateHandle;
        this.authRepo = authRepo;
        this.profileRepo = profileRepo;
        this.movieRepo = movieRepo;
        this.genreRepo = genreRepo;
        this.peopleRepo = peopleRepo;
    }

    public void loadInit() {
        if(!loadingSignal) {
            loadUserAvatar();
            loadUpComingMovie(); // test
            loadUserGenres();
            loadTrendingMovies();
            loadTopRatedMovies();
            loadPopularMovies();
            loadPlayingMovies();
            loadPopularPeople();
            loadRandomGenreMovie();
            loadTrendingPeople();
            loadingSignal = true;
        }
    }

    //-------------------------------------------------------Businesses-------------------------------------------------------
    // --------------------------User--------------------------
    private void loadUserAvatar() {
        String url = profileRepo.getUserAvatarUrl();
        userAvatarState.setValue(url);
    }

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
                }).subscribeOn(Schedulers.computation())
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
        genreRepo.updateUserGenres(authRepo.getUserUid(), userPeekGenres).addOnSuccessListener(runnable -> {
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

    // ------------------Preview Movies For RecyclerViews------------------
    /*@SuppressLint("CheckResult")
    private void loadUpComingMovie() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        imageUrls.add("https://media-cdn-v2.laodong.vn/Storage/NewsPortal/2022/11/22/1119442/Daniel-Craig.jpg");
        listUpcomingMoviePosterUrls.setValue(imageUrls);
    }*/

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadUpComingMovie() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_UPCOMING_TITLE)
                .subscribe(movies -> {
                    List<String> imageUrls = new ArrayList<>();
                    for(int i = 0; i < 5; i++) {
                        imageUrls.add(AppConstant.TMDB_IMAGE_HOST + movies.get(i).posterPath);
                    }
                    listUpcomingMoviePosterUrls.setValue(imageUrls);
                }, throwable -> Log.d("ERROR_UPCOMING", throwable.toString()));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadTrendingMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_TRENDING_TITLE)
                .subscribe((movies, throwable) -> previewTrendingMovies.setValue(movies));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadTopRatedMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_TOP_RATED_TITLE)
                .subscribe((movies, throwable) -> previewTopRatedMovies.setValue(movies));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPopularMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_POPULAR_TITLE).subscribe((movies, throwable)
                -> previewPopularMovies.setValue(movies));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPlayingMovies() {
        movieRepo.getPreviewMoviesByCategory(AppConstant.CATEGORY_PLAYING_TITLE).subscribe((movies, throwable)
                -> previewPlayingMovies.setValue(movies));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadRandomGenreMovie() {
        Single.fromCallable(() -> {
            int randomGenre = (int) (Math.random() * genreRepo.getUserGenres().size());
            return genreRepo.getUserGenres().get(randomGenre);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(genre -> {
            personalGenre.setValue(genre);
            loadListMovieForRandomGenre(String.valueOf(genre.getId()));
        });
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadListMovieForRandomGenre(String id) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("with_genres", id);
        filters.put("page", 1);
        movieRepo.discoverMovie(filters).subscribe(personalMovies::setValue,
                throwable -> {Log.d("ERROR", throwable.toString());});
    }

    // ------------------Preview People For RecyclerViews------------------
    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadPopularPeople() {
        peopleRepo.loadListPopularPeople()
                .subscribe((people, throwable) -> previewPopularPeople.setValue(people));
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    private void loadTrendingPeople() {
        peopleRepo.loadListTrendingPeople()
                .subscribe((people, throwable) -> previewTrendingPeople.setValue(people));
    }
}
