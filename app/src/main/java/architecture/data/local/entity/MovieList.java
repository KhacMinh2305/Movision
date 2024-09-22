package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "list_movie")
public class MovieList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "listId")
    public int listId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "total")
    public int total;

    @ColumnInfo(name = "tag")
    public String tag;

    public MovieList(int listId, String name, String description, int total, String tag) {
        this.listId = listId;
        this.name = name;
        this.description = description;
        this.total = total;
        this.tag = tag;
    }
}
