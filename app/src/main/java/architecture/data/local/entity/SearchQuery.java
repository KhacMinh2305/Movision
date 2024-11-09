package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_history")
public class SearchQuery {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "query")
    public String query;

    public SearchQuery(long id, String userId, String query) {
        this.id = id;
        this.userId = userId;
        this.query = query;
    }
}
