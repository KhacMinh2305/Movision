package architecture.data.model.movie.in_app;

public class ReviewAuthor {

    private String id;
    private String name;
    private String avatar_url;
    private long created_time;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public long getCreated_time() {
        return created_time;
    }

    public ReviewAuthor() {}

    public ReviewAuthor(String id, String name, String avatar_url, long created_time) {
        this.id = id;
        this.name = name;
        this.avatar_url = avatar_url;
        this.created_time = created_time;
    }
}
