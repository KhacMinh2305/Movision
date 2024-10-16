package architecture.data.source.other;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class FacebookAuthCallbackImpl implements FacebookAuthCallback {

    public interface OnResultCallback {
        void onSuccess(AccessToken accessToken);
        void onCancel();
        void onError(@NonNull FacebookException e);
    }

    private static CallbackManager mCallbackManager;

    @Inject
    public FacebookAuthCallbackImpl(@ApplicationContext Context context) {
        FacebookSdk.sdkInitialize(context); // move to init() for optimization
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void init(Fragment fragment, OnResultCallback callback) {
        LoginManager.getInstance().logInWithReadPermissions(fragment, mCallbackManager, List.of("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                callback.onSuccess(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
