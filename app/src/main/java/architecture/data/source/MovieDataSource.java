package architecture.data.source;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.AppDataStore;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.MovieDetails;
import architecture.data.local.entity.RemoteKey;
import architecture.data.model.movie.in_app.ClipUrl;
import architecture.data.model.movie.in_app.MovieItem;
import architecture.data.model.movie.in_app.MovieReview;
import architecture.data.model.movie.in_app.SimilarMovie;
import architecture.data.model.movie.result.ApiMovieResult;
import architecture.data.model.movie.category.ApiMovieDetails;
import architecture.data.model.movie.result.MovieClipResult;
import architecture.data.network.api.TmdbServices;
import architecture.data.source.other.CachingSource;
import architecture.data.source.other.CategoryMovieRemoteMediator;
import architecture.data.source.other.MovieReviewSource;
import architecture.data.source.other.SearchMovieSource;
import architecture.domain.MovieConversionHelper;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class MovieDataSource {
    private final LocalDatabase db;
    private final FirebaseFirestore cloud;
    private final AppDataStore dataStore;
    private final TmdbServices movieService;
    private final MovieDao movieDao;
    private final RemoteKeyDao keyDao;
    private final MovieGenreSource genreSource;
    private MovieReviewSource.AddingCallback reviewAddingCallback;
    private final CachingSource cachingSource;

    @Inject
    public MovieDataSource(LocalDatabase db, FirebaseFirestore cloud,
                           AppDataStore dataStore, TmdbServices movieService,
                           MovieDao movieDao, RemoteKeyDao keyDao,
                           MovieGenreSource genreSource, CachingSource cachingSource) {
        this.movieService = movieService;
        this.dataStore = dataStore;
        this.db = db;
        this.cloud = cloud;
        this.movieDao = movieDao;
        this.keyDao = keyDao;
        this.genreSource = genreSource;
        this.cachingSource = cachingSource;
    }

    // --------------------------------------load data for preview recycler views in home fragments--------------------------------------
    public Single<List<Movie>> loadPreviewDataByCategory(String category) {
        switch (category) {
            case AppConstant.CATEGORY_UPCOMING_TITLE -> {
                return loadPreviewUpcomingCategory();
            }
            case AppConstant.CATEGORY_TRENDING_TITLE -> {
                return loadPreviewTrendingCategory();
            }
            case AppConstant.CATEGORY_TOP_RATED_TITLE -> {
                return loadPreviewTopRatedCategory();
            }
            case AppConstant.CATEGORY_POPULAR_TITLE -> {
                return loadPreviewPopularCategory();
            }
            case AppConstant.CATEGORY_PLAYING_TITLE -> {
                return loadPreviewNowPlayingCategory();
            }
            default -> {
                return Single.just((List<Movie>) new ArrayList<Movie>())
                        .subscribeOn(Schedulers.single())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
    }

    private Single<List<Movie>> loadPreviewUpcomingCategory() {
        return movieService.loadPreviewUpcomingMovie(1)
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_UPCOMING_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewTrendingCategory() {
        return movieService.loadPreviewTrendingCategory("day")
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_TRENDING_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewTopRatedCategory() {
        return movieService.loadPreviewTopRatedCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_TOP_RATED_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewPopularCategory() {
        return movieService.loadPreviewPopularCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_POPULAR_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Movie>> loadPreviewNowPlayingCategory() {
        return movieService.loadPreviewPlayingCategory()
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_PLAYING_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Movie> handleData(List<ApiMovieResult> apiData, int totalPages, String tag) {
        List<Movie> data = (new MovieConversionHelper()).convertApiDataToLocalData(apiData, tag, genreSource.getAppGenres()); // process raw data
        int nextKey = (totalPages == 1) ? 1 : 2;
        cacheData(data, tag, nextKey);
        return data;
    }

    private void cacheData(List<Movie> result, String tag, int nextKey) {
        db.runInTransaction(() -> {
            // refresh data and key
            keyDao.deleteRemoteKeys(tag);
            movieDao.deleteMovies(tag);
            // insert data (note the case api only has 1 page of data. This cause error when load append in MovieRemoteMediator)
            RemoteKey remoteKey = new RemoteKey(nextKey, tag);
            keyDao.insertRemoteKey(remoteKey);
            movieDao.insertMovies(result);
        });
    }

    // --------------------------------------load movie with genre--------------------------------------
    public Single<List<Movie>> discoverMovie(Map<String, Object> filters) {
        return movieService.loadMovieByGenre(filters)
                .subscribeOn(Schedulers.single())
                .map(apiMovie -> handleData(apiMovie.getResults(),
                        apiMovie.getTotalPages(), AppConstant.CATEGORY_UNDEFINED_TAG))
                .observeOn(AndroidSchedulers.mainThread());
    }

    // --------------------------------------Create Pager for PagingAdapter--------------------------------------
    public Pager<Integer, Movie> getMoviePager(String tag, int pageSize) {
        CategoryMovieRemoteMediator movieMediator =
                new CategoryMovieRemoteMediator(db, movieService, movieDao, keyDao, genreSource.getAppGenres());
        movieMediator.setMovieInfo(tag);
        Pager<Integer, Movie> pager = new Pager(new PagingConfig(pageSize), null,
                movieMediator,
                () -> movieDao.moviePagingSource(tag));
        return pager;
    }

    // --------------------------------------other--------------------------------------
    public Single<Movie> getMovie(int movieId) {
        return movieDao.findMovieById(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MovieDetails> getMovieDetails(int movieId) {
        return movieDao.getMovieDetails(movieId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable ->
                        movieService.loadMovieDetails(movieId).subscribeOn(Schedulers.single())
                        .doOnSuccess(details -> {
                            MovieDetails movieDetails = details.toMovieDetails();
                            db.runInTransaction(() -> movieDao.insertMovieDetails(movieDetails));
                        }).map(ApiMovieDetails::toMovieDetails).observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<List<ClipUrl>> getMovieClips(int movieId) {
        return movieService.loadMovieClips(movieId)
                .subscribeOn(Schedulers.single())
                .map(apiMovieClip -> {
                    List<ClipUrl> list = new ArrayList<>();
                    for(MovieClipResult result : apiMovieClip.getResults()) {
                        list.add(new ClipUrl(result.getName(), AppConstant.YOUTUBE_EMBED_URL + result.getKey()));
                    }
                    return list;
                }).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SimilarMovie>> loadSimilarMovie(int movieId, int page) {
        return movieService.loadSimilarMovies(movieId, page)
                .subscribeOn(Schedulers.single())
                .map(similarApiMovie -> {
                    List<ApiMovieResult> results = similarApiMovie.getResults();
                    List<SimilarMovie> similarMovies = new ArrayList<>();
                    for (ApiMovieResult result : results) {
                        similarMovies.add(result.toSimilarMovie());
                    }
                    return similarMovies;
                }).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable rateMovie(int movieId, float rating) {
        return Single.fromCallable(() -> {
            String cachedSession = dataStore.getKey(AppConstant.SESSION_ID);
            if(!cachedSession.isEmpty()) {
                return cachedSession;
            }
            JsonObject result = movieService.requestSession().blockingGet();
            String session = result.get("guest_session_id").getAsString();
            dataStore.saveKeyAsync(AppConstant.SESSION_ID, session);
            return session;
        }).subscribeOn(Schedulers.single()).flatMapCompletable(session -> {
            JsonObject ratingObject = new JsonObject();
            ratingObject.addProperty("value", rating);
            return movieService.addRating(movieId, session, ratingObject).subscribeOn(Schedulers.single());
        }).observeOn(AndroidSchedulers.mainThread());
    }

    private Map<String, Object> createNewRatingRecord(String movieName, String posterPath,
                                                      double movieRating, double userRating) {
        Map<String, Object> record = new HashMap<>();
        record.put("movie_name", movieName);
        record.put("poster_path", posterPath);
        record.put("movie_rating", movieRating);
        record.put("user_rating", userRating);
        return record;
    }

    public PublishSubject<Boolean> addRatingToDatabase(String userId, int movieId, String movieName, double movieRating,
                                               double userRating, String posterPath) {
        PublishSubject<Boolean> result = PublishSubject.create();
        cloud.collection("rating").document(userId)
                .collection("records").document(String.valueOf(movieId))
                .set(createNewRatingRecord(movieName, posterPath, movieRating, userRating), SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    result.onNext(true);
                    result.onComplete();
                }).addOnFailureListener(e -> {
                    result.onError(e);
                    result.onComplete();
                });
        return result;
    }

    public Task<Double> getRatingOfMovie(String userId, int movieId) {
        return cloud.collection("rating").document(userId)
                .collection("records").document(String.valueOf(movieId))
                .get().onSuccessTask(documentSnapshot -> {
                    Map<String, Object> data = documentSnapshot.getData();
                    if(data == null || data.isEmpty() || !data.containsKey("user_rating")) {
                        return Tasks.forResult(-1d);
                    }
                    Double userRating = (Double) data.get("user_rating");
                    return Tasks.forResult(userRating);
                });
    }

    private Map<String, Object> createNewFavoriteRecord(String movieName, String posterPath, double movieRating) {
        Map<String, Object> record = new HashMap<>();
        record.put("movie_name", movieName);
        record.put("poster_path", posterPath);
        record.put("movie_rating", movieRating);
        return record;
    }

    public Task<Void> addToFavoriteList(String userId, int movieId, String movieName,
                                           double movieRating, String posterPath) {
        return cloud.collection("favorite").document(userId)
            .collection("records").document(String.valueOf(movieId))
            .set(createNewFavoriteRecord(movieName, posterPath, movieRating), SetOptions.merge());
    }

    public Task<Void> removeFromFavoriteList(String userId, int movieId) {
        return cloud.collection("favorite").document(userId)
                .collection("records").document(String.valueOf(movieId))
                .delete();
    }

    public Task<Boolean> checkMovieFavorite(String userId, int movieId) {
        return cloud.collection("favorite").document(userId)
                .collection("records").document(String.valueOf(movieId))
                .get().onSuccessTask(documentSnapshot -> {
                    Map<String, Object> data = documentSnapshot.getData();
                    return Tasks.forResult(data != null && !data.isEmpty());
                });
    }

    public Completable addMovieReviews(int movieId, MovieReview review) {
        Task<Void> insertionTask = cloud.collection("movie_review").document(String.valueOf(movieId))
                .collection("records").document(review.getId())
                .set(review, SetOptions.merge());
        return Completable.fromAction(() ->
                Tasks.await(insertionTask)).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread());
    }

    public Pager<Long, MovieReview> getMovieReviewPager(int movieId) {
        Pager<Long, MovieReview> pager = new Pager<>(new PagingConfig(10),
                () -> new MovieReviewSource(cloud, movieId, 10));
        return pager;
    }

    public Pager<Integer, MovieItem> getMovieSearchPager(String query) {
        SearchMovieSource source = new SearchMovieSource(movieService, cachingSource, query);
        source.init();
        Pager<Integer, MovieItem> pager = new Pager<>(new PagingConfig(20),
                () -> source);
        return pager;
    }
}