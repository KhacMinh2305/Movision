package architecture.domain;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import architecture.data.model.genre.Genre;

public class ListProcessingHelper {

    public ListProcessingHelper() {

    }

    public void sortList(List<Genre> list) {
        list.sort((g1, g2) -> {
            if(g1.getId() > g2.getId()) {
                return 1;
            } else if(Objects.equals(g1.getId(), g2.getId())) {
                return 0;
            }
            return - 1;
        });
    }

    public List<Long> retrieveListIds(List<Genre> genres) {
        List<Long> ids = new ArrayList<>();
        for(Genre genre : genres) {
            ids.add(genre.getId());
        }
        return ids;
    }

    public List<Genre> transformRawListToGenresList(DocumentSnapshot documentSnapshot, List<Genre> appGenres) {
        sortList(appGenres);
        List<Genre> userGenres = new ArrayList<>();
        List<Long> genreIdList = retrieveRawGenresList(documentSnapshot);
        for (long id : genreIdList) {
            Genre genre = getGenreById(id, appGenres);
            if (genre != null) {
                userGenres.add(genre);
            }
        }
        return userGenres;
    }

    /** @noinspection unchecked*/
    public List<Long> retrieveRawGenresList(DocumentSnapshot documentSnapshot) {
        List<Long> genreIdList;
        genreIdList = (List<Long>) documentSnapshot.get("genresId");
        return genreIdList;
    }

    public static Genre getGenreById(long id, List<Genre> appGenres) {
        int left = 0, right = appGenres.size() - 1;
        Genre genre = null;
        while(left <= right) {
            int mid = (left + right)  / 2;
            genre = appGenres.get(mid);
            if(genre.getId() == id) {
                return genre;
            } else if(genre.getId() > id) {
                right = mid - 1;
                continue;
            }
            left = mid + 1;
        }
        return genre;
    }
}
