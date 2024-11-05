package architecture.di;
import android.content.Context;
import androidx.credentials.CredentialManager;
import androidx.room.Room;
import javax.inject.Singleton;
import architecture.data.local.LocalDatabase;
import architecture.data.local.dao.ListDao;
import architecture.data.local.dao.MovieDao;
import architecture.data.local.dao.PeopleDao;
import architecture.data.local.dao.RemoteKeyDao;
import architecture.data.local.dao.SearchHistoryDao;
import architecture.other.AppConstant;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LocalComponentsProvider {

    @Provides
    @Singleton
    public static CredentialManager provideCredentialManager(@ApplicationContext Context context) {
        return CredentialManager.create(context);
    }

    @Provides
    @Singleton
    public static LocalDatabase provideLocalDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, LocalDatabase.class, AppConstant.LOCAL_DATABASE_NAME)
                .build();
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

    @Provides
    @Singleton
    public static SearchHistoryDao provideSearchHistoryDao(LocalDatabase localDatabase) {
        return localDatabase.searchHistoryDao();
    }
}
