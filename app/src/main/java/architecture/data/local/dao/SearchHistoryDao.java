package architecture.data.local.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import architecture.data.local.entity.SearchQuery;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SearchHistoryDao {

    @Insert(entity = SearchQuery.class, onConflict = OnConflictStrategy.REPLACE)
    void insertQuery(SearchQuery query);

    @Query("DELETE FROM search_history WHERE id = :id")
    void deleteQuery(long id);

    @Query("DELETE FROM search_history")
    void deleteAllHistory();

    @Query("SELECT * FROM search_history")
    Single<List<SearchQuery>> getAllHistory();
}
