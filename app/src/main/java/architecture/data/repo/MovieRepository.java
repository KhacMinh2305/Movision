package architecture.data.repo;
import androidx.paging.Pager;
import com.google.android.gms.tasks.Task;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.MovieDetails;
import architecture.data.model.movie.in_app.ClipUrl;
import architecture.data.model.movie.in_app.MovieReview;
import architecture.data.model.movie.in_app.SimilarMovie;
import architecture.data.source.MovieDataSource;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class MovieRepository {
    private final MovieDataSource dataSource;

    @Inject
    public MovieRepository(MovieDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<List<Movie>> getPreviewMoviesByCategory(String category) {
        return dataSource.loadPreviewDataByCategory(category);
    }

    public Pager<Integer, Movie> getMoviePager(String tag, int pageSize) {
        return dataSource.getMoviePager(tag, pageSize);
    }

    public Single<List<Movie>> discoverMovie(Map<String, Object> filters) {
        return dataSource.discoverMovie(filters);
    }

    public Single<Movie> getMovie(int movieId) {
        return dataSource.getMovie(movieId);
    }

    public Single<MovieDetails> getMovieDetails(int movieId) {
        return dataSource.getMovieDetails(movieId);
    }

    public Single<List<ClipUrl>> getMovieClips(int movieId) {
        return dataSource.getMovieClips(movieId);
    }

    public Single<List<SimilarMovie>> loadSimilarPeople(int movieId, int page) {
        return dataSource.loadSimilarMovie(movieId, page);
    }

   /* public Completable rateMovie(int movieId, float rating) {
        return dataSource.rateMovie(movieId, rating);
    }*/

    public PublishSubject<Boolean> rateMovie(String userId, int movieId, String movieName, double movieRating,
                                             double userRating, String posterPath) {
        return dataSource.addRatingToDatabase(userId, movieId, movieName, movieRating, userRating, posterPath);
    }

    public Task<Double> getRatingOfMovie(String userId, int movieId) {
        return dataSource.getRatingOfMovie(userId, movieId);
    }


    public Task<Void> addToFavoriteList(String userId, int movieId, String movieName,
                                                     double movieRating, String posterPath) {
        return dataSource.addToFavoriteList(userId, movieId, movieName, movieRating, posterPath);
    }

    public Task<Void> removeFromFavoriteList(String userId, int movieId) {
        return dataSource.removeFromFavoriteList(userId, movieId);
    }

    public Task<Boolean> checkMovieFavorite(String userId, int movieId) {
        return dataSource.checkMovieFavorite(userId, movieId);
    }

    public Completable addMovieReviews(int movieId, MovieReview review) {
        return dataSource.addMovieReviews(movieId, review);
    }

    public Pager<Long, MovieReview> getMovieReviewPager(int movieId) {
        return dataSource.getMovieReviewPager(movieId);
    }
}