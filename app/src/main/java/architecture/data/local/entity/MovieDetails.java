package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_details")
public class MovieDetails {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "movie_id")
    public int movieId;

    @ColumnInfo(name = "language")
    public String language;

    @ColumnInfo(name = "run_time")
    public int runtime;

    @ColumnInfo(name = "added_watch_list")
    public boolean addedToWatchList;

    @ColumnInfo(name = "favourite")
    public boolean favourite;

    @ColumnInfo(name = "rated")
    public double rated;

    public MovieDetails(long id, int movieId, String language,
                        int runtime, boolean addedToWatchList,
                        boolean favourite, double rated) {
        this.id = id;
        this.movieId = movieId;
        this.language = language;
        this.runtime = runtime;
        this.addedToWatchList = addedToWatchList;
        this.favourite = favourite;
        this.rated = rated;
    }
}