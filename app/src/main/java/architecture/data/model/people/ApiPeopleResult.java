package architecture.data.model.people;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import architecture.data.local.entity.People;

public class ApiPeopleResult {

    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("known_for_department")
    @Expose
    private String knownForDepartment;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    @SerializedName("known_for")
    @Expose
    private List<KnownMovie> knownMovies;

    public Boolean getAdult() {
        return adult;
    }

    public Integer getGender() {
        return gender;
    }

    public Integer getId() {
        return id;
    }

    public String getKnownForDepartment() {
        return knownForDepartment;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public List<KnownMovie> getKnownMovies() {
        return knownMovies;
    }

    public People toPeople() {
        return new People(this.id, this.name, this.gender, this.knownForDepartment, this.profilePath);
    }

    public PeopleItem toPeopleItem() {
        String gender = "Undefined";
        gender = this.gender == 1 ? "Female" : gender;
        gender = this.gender == 2 ? "Male" : gender;
        return new PeopleItem(this.id, this.name, gender, this.profilePath);
    }
}