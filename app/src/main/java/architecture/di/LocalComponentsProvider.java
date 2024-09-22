package architecture.di;
import android.content.Context;
import androidx.room.Room;
import javax.inject.Singleton;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.ListDao;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.PeopleDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.dao.UserDao;
import architecture.other.AppConstant;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class LocalComponentsProvider {

    @Provides
    @Singleton
    public static LocalDatabase provideLocalDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, LocalDatabase.class, AppConstant.LOCAL_DATABASE_NAME)
                .build();
    }

    @Provides
    @Singleton
    public static UserDao provideUserDao(LocalDatabase localDatabase) {
        return localDatabase.userDao();
    }

    @Provides
    @Singleton
    public static MovieDao provideMovieDao(LocalDatabase localDatabase) {
        return localDatabase.movieDao();
    }

    @Provides
    @Singleton
    public static ListDao provideListDao(LocalDatabase localDatabase) {
        return localDatabase.listDao();
    }

    @Provides
    @Singleton
    public static PeopleDao providePeopleDao(LocalDatabase localDatabase) {
        return localDatabase.peopleDao();
    }

    @Provides
    @Singleton
    public static RemoteKeyDao provideRemoteKeyDao(LocalDatabase localDatabase) {
        return localDatabase.remoteKeyDao();
    }
}
