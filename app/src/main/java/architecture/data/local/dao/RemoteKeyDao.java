package architecture.data.local.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import architecture.data.local.entity.RemoteKey;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRemoteKey(RemoteKey key);

    @Query("DELETE FROM remote_keys WHERE tag = :tag")
    void deleteRemoteKeys(String tag);

    @Query("SELECT * FROM remote_keys WHERE tag LIKE :remoteKeyTag ORDER BY next_key DESC LIMIT 1")
    Single<RemoteKey> getKey(String remoteKeyTag);
}
