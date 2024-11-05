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

    @ColumnInfo(name = "tag")
    public String tag;

    public SearchQuery(long id, String userId, String query, String tag) {
        this.id = id;
        this.userId = userId;
        this.query = query;
        this.tag = tag;
    }
}
