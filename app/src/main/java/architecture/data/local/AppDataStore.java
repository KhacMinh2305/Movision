package architecture.data.local;
import android.content.Context;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.other.AppConstant;
import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class AppDataStore {
    private RxDataStore<Preferences> dataStore;

    private static Preferences.Key<String> apiKey;
    private static Preferences.Key<String> readAccessTokenKey;
    private static Preferences.Key<String> accessTokenKey;
    private static Preferences.Key<String> sessionKey;
    private static Preferences.Key<String> usernameKey;
    private static Preferences.Key<String> passwordKey;

    @Inject
    public AppDataStore(@ApplicationContext Context context) {
        init(context);
    }

    private void init(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, AppConstant.FILE_NAME).build();
        apiKey = PreferencesKeys.stringKey(AppConstant.API_KEY_NAME);
        readAccessTokenKey = PreferencesKeys.stringKey(AppConstant.READ_ACCESS_TOKEN_NAME);
        accessTokenKey = PreferencesKeys.stringKey(AppConstant.ACCESS_TOKEN_NAME);
        sessionKey = PreferencesKeys.stringKey(AppConstant.SESSION_ID_NAME);
        usernameKey = PreferencesKeys.stringKey(AppConstant.USERNAME_NAME);
        passwordKey = PreferencesKeys.stringKey(AppConstant.PASSWORD_NAME);
    }

    private Preferences.Key<String> findKey(int key) {
        if(key == 0) {
            return apiKey;
        } else if(key == 1) {
            return readAccessTokenKey;
        } else if(key == 2) {
            return accessTokenKey;
        } else if(key == 3) {
            return sessionKey;
        } else if(key == 4) {
            return usernameKey;
        } else if(key == 5) {
            return passwordKey;
        }
        return null;
    }

    public String getKey(int key) {
        Preferences.Key<String> keyToGet = findKey(key);
        return (keyToGet != null) ? dataStore.data().map(preferences -> (preferences.contains(keyToGet)
                ? preferences.get(keyToGet) : "")).blockingFirst() : "";
    }

    public Flowable<String> subscribeForGettingKey(int key) {
        Preferences.Key<String> keyToGet = findKey(key);
        if(keyToGet == null) {
            return Flowable.just("");
        }
        return dataStore.data().map(preferences -> preferences.get(keyToGet)).subscribeOn(Schedulers.io());
    }

    public @NonNull Single<Preferences> subscribeForSavingKey(int key, String value) {
        Preferences.Key<String> savedKey = findKey(key);
        return dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            assert savedKey != null;
            mutablePreferences.set(savedKey, value);
            return Single.just(mutablePreferences);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveKey(int key, String value) {
        subscribeForSavingKey(key, value).subscribe();
    }

    private void clearData() {
        saveKey(AppConstant.ACCESS_TOKEN, "");
        saveKey(AppConstant.SESSION_ID, "");
        saveKey(AppConstant.USERNAME, "");
        saveKey(AppConstant.PASSWORD, "");
    }

    /*private void initData() {
        saveKey(AppConstant.API_KEY, "");
        saveKey(AppConstant.READ_ACCESS_TOKEN, "c25SORBFbz5FLkIbHgpQhM");
    }*/
}

// xu ly bang cach luu lai thoi gian expired ma session cua user het han. Sau do