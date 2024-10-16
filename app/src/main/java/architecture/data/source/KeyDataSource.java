package architecture.data.source;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.local.AppDataStore;
import architecture.data.network.api.TmdbServices;
import architecture.domain.JsonConverterHelper;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class KeyDataSource {
    private final AppDataStore dataStore;
    private final TmdbServices tmdbServices;
    private JsonConverterHelper converter;

    @Inject
    public KeyDataSource(AppDataStore dataStore, TmdbServices tmdbServices) {
        this.dataStore = dataStore;
        this.tmdbServices = tmdbServices;
        converter = new JsonConverterHelper();
    }

    public void saveAccountInfo(String username) {
        dataStore.saveKey(AppConstant.USERNAME, username);
    }

    public void saveSessionId(String sessionId) {
        dataStore.saveKey(AppConstant.SESSION_ID, sessionId);
    }

    public Single<String> getSessionId() {
        return Single.fromCallable(() -> dataStore.getKey(AppConstant.SESSION_ID))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> requestSessionId(String username, String password) {
        return Single.fromCallable(() -> {
            String accessToken = dataStore.getKey(AppConstant.ACCESS_TOKEN);
            return converter.createJsonObject(Map.of("username", username,
                    "password", password, "request_token", accessToken));
        }).subscribeOn(Schedulers.io())
                .flatMap(body -> tmdbServices.requestSessionId(body)
                        .subscribeOn(Schedulers.single())
                        .map(result -> {
                            String session = result.get("request_token").getAsString();
                            dataStore.saveKey(AppConstant.SESSION_ID, session);
                            return result.get("expires_at").getAsString();
                        }))
                .onErrorReturn(throwable -> "")
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void clearLoginInfoOnFailure() {
        dataStore.saveKey(AppConstant.ACCESS_TOKEN, "");
    }
}
