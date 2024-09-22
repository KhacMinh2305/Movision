package architecture.data.repo;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.rxjava3.PagingRx;

import java.util.List;

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
}