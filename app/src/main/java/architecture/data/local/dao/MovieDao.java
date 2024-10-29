package architecture.data.local.dao;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.MovieDetails;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MovieDao {

    @Insert(entity = Movie.class, onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    @Query("DELETE FROM movie WHERE tag = :tag")
    void deleteMovies(String tag);

    @Query("SELECT * FROM movie WHERE tag LIKE :tag")
    PagingSource<Integer, Movie> moviePagingSource(String tag);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId LIMIT 1")
    Single<Movie> findMovieById(int movieId);

    @Query("SELECT * FROM movie WHERE tag LIKE :category LIMIT 10")
    List<Movie> getMoviesByCategory(String category);

    @Insert(entity = MovieDetails.class, onConflict = OnConflictStrategy.REPLACE)
    void insertMovieDetails(MovieDetails movieDetail);

    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    Single<MovieDetails> getMovieDetails(int movieId);
}
