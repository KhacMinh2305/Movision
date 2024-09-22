package architecture.data.local.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user", indices = {@Index("username")})
public class User {

    @PrimaryKey
    @ColumnInfo(name = "username")
    @NonNull
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "session_expired")
    public String expiredTime;

    public User(@NonNull String username, String password, String expiredTime) {
        this.username = username;
        this.password = password;
        this.expiredTime = expiredTime;
    }
}
