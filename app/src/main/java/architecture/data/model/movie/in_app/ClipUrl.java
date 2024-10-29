package architecture.data.model.movie.in_app;

public class ClipUrl {

    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public ClipUrl(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
