package architecture.data.model.movie.in_app;

public class MovieReview {
    private String id;
    private String content;
    private ReviewAuthor author;
    private long created_time;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public ReviewAuthor getAuthor() {
        return author;
    }

    public long getCreated_time() {
        return created_time;
    }

    public MovieReview() {}

    public MovieReview(String id, String content, long created_time, ReviewAuthor author) {
        this.id = id;
        this.content = content;
        this.created_time = created_time;
        this.author = author;
    }
}
