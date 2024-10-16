package architecture.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import java.util.List;

import architecture.data.local.entity.People;

@Dao
public interface PeopleDao {

    @Insert(entity = People.class, onConflict = OnConflictStrategy.REPLACE)
    void insertPeople(List<People> people);
}
