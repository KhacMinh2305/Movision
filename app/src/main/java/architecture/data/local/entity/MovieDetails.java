package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_details", indices = {@Index(value = "name")})
public class MovieDetails {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int movieId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "genres")
    public String genres;

    @ColumnInfo(name = "overview")
    public String overview;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "rating")
    public double rating;

    @ColumnInfo(name = "language")
    public String language;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    public MovieDetails(int movieId, String name, String genres,
                        String overview, int duration, int year,
                        double rating, String language, String posterPath) {
        this.movieId = movieId;
        this.name = name;
        this.genres = genres;
        this.overview = overview;
        this.duration = duration;
        this.year = year;
        this.rating = rating;
        this.language = language;
        this.posterPath = posterPath;
    }
}