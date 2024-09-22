package architecture.data.local.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "people")
public class People {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "gender")
    public int gender;

    @ColumnInfo(name = "department")
    public String department;

    @ColumnInfo(name = "profile_path")
    public String profilePath;

    public People(int id, String name, int gender, String department, String profilePath) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.department = department;
        this.profilePath = profilePath;
    }
}
