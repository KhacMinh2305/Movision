package architecture.data.repo;
import androidx.paging.Pager;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.entity.Movie;
import architecture.data.source.MovieDataSource;
import io.reactivex.rxjava3.core.Single;

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
}