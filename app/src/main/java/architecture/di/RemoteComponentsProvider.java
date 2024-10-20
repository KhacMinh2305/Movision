package architecture.di;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;
import architecture.data.network.api.TmdbServices;
import architecture.data.network.api.HeaderInterceptor;
import architecture.data.source.other.FacebookAuthCallback;
import architecture.data.source.other.FacebookAuthCallbackImpl;
import architecture.other.AppConstant;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RemoteComponentsProvider {

    @Binds
    public abstract FacebookAuthCallback bindFacebookAuthCallback(FacebookAuthCallbackImpl impl);

    @Provides
    @Singleton
    public static TmdbServices provideTmdbServices(HeaderInterceptor interceptor) {
        return new Retrofit.Builder()
                .baseUrl(AppConstant.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                .build().create(TmdbServices.class);
    }

    @Provides
    @Singleton
    public static FirebaseFirestore provideCloudFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseStorage provideFirebaseStorageInstance() {
        return FirebaseStorage.getInstance();
    }
}
