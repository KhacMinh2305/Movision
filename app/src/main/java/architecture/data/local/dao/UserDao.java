package architecture.data.local.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import architecture.data.local.entity.User;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert(entity = User.class, onConflict = OnConflictStrategy.REPLACE)
    void addUser(User... user);

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    Single<User> getUser(String username, String password);
}
