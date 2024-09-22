package architecture.data.local;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import architecture.data.local.dao.ListDao;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.PeopleDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.dao.UserDao;
import architecture.data.local.entity.Movie;
import architecture.data.local.entity.MovieDetails;
import architecture.data.local.entity.MovieList;
import architecture.data.local.entity.People;
import architecture.data.local.entity.PeopleDetails;
import architecture.data.local.entity.RemoteKey;
import architecture.data.local.entity.User;

@Database(entities = {User.class, Movie.class, MovieDetails.class,
        MovieList.class, People.class, PeopleDetails.class, RemoteKey.class}, version = 2, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract MovieDao movieDao();
    public abstract ListDao listDao();
    public abstract PeopleDao peopleDao();
    public abstract RemoteKeyDao remoteKeyDao();
}
