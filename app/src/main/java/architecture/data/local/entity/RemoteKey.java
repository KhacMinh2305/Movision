package architecture.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "remote_keys", primaryKeys = {"next_key", "tag"})
public class RemoteKey {

    @ColumnInfo(name = "next_key")
    public int nextKey;

    @ColumnInfo(name = "tag")
    @NonNull
    public String tag; // this field is used to distinguish which RemoteKey that a consumer is using.

    public RemoteKey(int nextKey, @NonNull String tag) {
        this.nextKey = nextKey;
        this.tag = tag;
    }
}
