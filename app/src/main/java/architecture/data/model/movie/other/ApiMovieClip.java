package architecture.data.model.movie.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import architecture.data.model.movie.result.MovieClipResult;

public class ApiMovieClip {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieClipResult> results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieClipResult> getResults() {
        return results;
    }

    public void setResults(List<MovieClipResult> results) {
        this.results = results;
    }
}
