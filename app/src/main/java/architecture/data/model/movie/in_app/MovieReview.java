package architecture.data.model.movie.in_app;

public class MovieReview {
    private String id;
    private String content;
    private ReviewAuthor author;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public ReviewAuthor getAuthor() {
        return author;
    }

    public MovieReview() {}

    public MovieReview(String id, String content, ReviewAuthor author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }
}
