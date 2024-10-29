package architecture.data.model.movie.in_app;

public class SimilarMovie {

    private int movieId;
    private String name;
    private String posterPath;

    public int getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public SimilarMovie(int movieId, String name, String posterPath) {
        this.movieId = movieId;
        this.name = name;
        this.posterPath = posterPath;
    }
}
