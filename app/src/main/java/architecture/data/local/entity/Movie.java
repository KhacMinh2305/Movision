package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "movie")
public class Movie {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "movie_id")
    public int movieId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "genres")
    public String genres;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "released_date")
    public String releaseDate;

    @ColumnInfo(name = "vote_average")
    public double voteAverage;

    @ColumnInfo(name = "vote_count")
    public int voteCount;

    @ColumnInfo(name = "backdrop_path")
    public String backdropPath;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    @ColumnInfo(name = "tag")
    public String tag;

    @Ignore
    public List<Integer> genresIds;

    @Ignore
    public Movie() {
        this.id = -1;
    }

    public Movie(int movieId, String title, String genres, String description,
                 String releaseDate, double voteAverage, int voteCount,
                 String backdropPath, String posterPath, String tag) {
        this.movieId = movieId;
        this.title = title;
        this.genres = genres;
        this.description = description;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.backdropPath = backdropPath;
        this.posterPath = posterPath;
        this.tag = tag;
        genresIds  = new ArrayList<>();
    }
}
