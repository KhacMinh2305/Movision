package architecture.data.network.api;
import android.util.Log;

import androidx.annotation.NonNull;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.AppDataStore;
import architecture.other.AppConstant;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class HeaderInterceptor implements Interceptor {

    private final AppDataStore dataStore;

    @Inject
    public HeaderInterceptor(AppDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;
        String readAccessToken = dataStore.getKey(AppConstant.READ_ACCESS_TOKEN);
        String methodType = request.method();
        if(methodType.equals("GET")) {
            newRequest = request.newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + readAccessToken)
                    .build();
            return chain.proceed(newRequest);
        }
        newRequest = request.newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", "Bearer " + readAccessToken)
                .build();
        return chain.proceed(newRequest);
    }
}
