package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "people_details")
public class PeopleDetails {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "biography")
    public String biography;

    @ColumnInfo(name = "department")
    public String department;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    @ColumnInfo(name = "birth_day")
    public String birthDay;

    @ColumnInfo(name = "death_day")
    public String deathDay;

    @ColumnInfo(name = "place_of_birth")
    public String placeOfBirth;

    public PeopleDetails(int id, String name, String biography, String department, String posterPath,
                         String birthDay, String deathDay, String placeOfBirth) {
        this.id = id;
        this.name = name;
        this.biography = biography;
        this.department = department;
        this.posterPath = posterPath;
        this.birthDay = birthDay;
        this.deathDay = deathDay;
        this.placeOfBirth = placeOfBirth;
    }
}
