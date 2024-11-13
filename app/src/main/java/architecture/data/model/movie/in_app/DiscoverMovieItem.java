package architecture.data.model.movie.in_app;

public class DiscoverMovieItem {
    private int id;
    private String name;
    private String overview;
    private double voteAverage;
    private String posterPath;

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getName() {
        return name;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public DiscoverMovieItem(int id, String name, String overview, double voteAverage, String posterPath) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
    }
}
