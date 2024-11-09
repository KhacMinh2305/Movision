package architecture.data.model.movie.in_app;

public class MovieItem {

    private int id;
    private String name;
    private double voteAverage;
    private String posterPath;

    public int getId() {
        return id;
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

    public MovieItem(int id, String name, double voteAverage, String posterPath) {
        this.id = id;
        this.name = name;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
    }
}
