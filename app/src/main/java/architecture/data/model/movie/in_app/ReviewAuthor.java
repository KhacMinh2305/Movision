package architecture.data.model.movie.in_app;

public class ReviewAuthor {

    private String id;
    private String name;
    private String avatar_url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public ReviewAuthor() {}

    public ReviewAuthor(String id, String name, String avatar_url) {
        this.id = id;
        this.name = name;
        this.avatar_url = avatar_url;
    }
}
