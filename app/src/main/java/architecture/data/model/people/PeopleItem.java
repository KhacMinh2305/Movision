package architecture.data.model.people;

public class PeopleItem {

    private int id;
    private String name;
    private String gender;
    private String posterPath;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public PeopleItem(int id, String name, String gender, String posterPath) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.posterPath = posterPath;
    }
}
