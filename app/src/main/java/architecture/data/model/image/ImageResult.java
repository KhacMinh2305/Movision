package architecture.data.model.image;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResult {

    @SerializedName("aspect_ratio")
    @Expose
    private Double aspectRatio;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("iso_639_1")
    @Expose
    private Object iso6391;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @SerializedName("width")
    @Expose
    private Integer width;

    public Double getAspectRatio() {
        return aspectRatio;
    }

    public Integer getHeight() {
        return height;
    }

    public Object getIso6391() {
        return iso6391;
    }

    public String getFilePath() {
        return filePath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Integer getWidth() {
        return width;
    }
}
