package architecture.data.source.other;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.model.movie.in_app.MovieItem;
import architecture.data.model.people.PeopleItem;

@Singleton
public class CachingSource {

    public record MovieItemCache(int page, int totalPages, List<MovieItem> data) {}

    public record PeopleItemCache(int page, int totalPages, List<PeopleItem> data) {}

    private final Map<String, List<MovieItemCache>> searchMovieResultMap = new HashMap<>();
    private final Map<String, List<PeopleItemCache>> searchPeopleResultMap = new HashMap<>();

    @Inject
    public CachingSource() { }

    public List<MovieItemCache> getSearchMovieCachedResult(String query) {
        String key = query.trim().toUpperCase();
        if(searchMovieResultMap.containsKey(key)) {
            return searchMovieResultMap.get(key);
        }
        return null;
    }

    public List<PeopleItemCache> getSearchPeopleCachedResult(String query) {
        String key = query.trim().toUpperCase();
        if(searchPeopleResultMap.containsKey(key)) {
            return searchPeopleResultMap.get(key);
        }
        return null;
    }

    public void cacheSearchMovieQuery(String query, List<MovieItem> data, int page, int totalPages) {
        if(data == null || data.isEmpty()) return;
        MovieItemCache item = new MovieItemCache(page, totalPages, data);
        String key = query.trim().toUpperCase();
        if(searchMovieResultMap.containsKey(key)) {
            Objects.requireNonNull(searchMovieResultMap.get(key)).add(item);
            return;
        }
        if(page == 1 && !searchMovieResultMap.containsKey(key)) {
            List<MovieItemCache> list = new ArrayList<>();
            list.add(item);
            searchMovieResultMap.put(key, list);
        }
    }

    public void cacheSearchPeopleQuery(String query, List<PeopleItem> data, int page, int totalPages) {
        if(data == null || data.isEmpty()) return;
        PeopleItemCache item = new PeopleItemCache(page, totalPages, data);
        String key = query.trim().toUpperCase();
        if(searchPeopleResultMap.containsKey(key)) {
            Objects.requireNonNull(searchPeopleResultMap.get(key)).add(item);
            return;
        }
        if(page == 1 && !searchPeopleResultMap.containsKey(key)) {
            List<PeopleItemCache> list = new ArrayList<>();
            list.add(item);
            searchPeopleResultMap.put(key, list);
        }
    }
}
