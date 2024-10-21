package architecture.data.repo;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.data.source.AuthenticationDataSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class AuthenticationRepository {
    private final AuthenticationDataSource authSource;

    @Inject
    public AuthenticationRepository(AuthenticationDataSource authSource) {
        this.authSource = authSource;
    }

    public String getUserUid() {
        return authSource.getUserUid();
    }

    public void updateUser() {
        authSource.updateUser();
    }

    public boolean checkIfSignedIn() {
        return authSource.checkIfSignedIn();
    }

    public Task<AuthResult> signInUserWithGmailAndPassword(String gmail, String password) {
        return authSource.signInUserWithGmailAndPassword(gmail, password);
    }

    public Task<AuthResult> signUpWithEmailAndPassword(String username, String password) {
        return authSource.signUpWithEmailAndPassword(username, password);
    }

    public PublishSubject<Boolean> loginWithGoogle(Context activityContext) {
        return authSource.getGoogleLoginState(activityContext);
    }

    public PublishSubject<String> getFacebookLoginState(Fragment fragment) {
        return authSource.getFacebookLoginState(fragment);
    }

    public void getResultFromFacebookTokenRequest(int requestCode, int resultCode, @Nullable Intent data) {
        authSource.getResultFromFacebookTokenRequest(requestCode, resultCode, data);
    }

    public Single<Boolean> sendVerificationCodeToEmail(String gmail) {
        return authSource.sendVerificationCodeToEmail(gmail);
    }

    public boolean checkCode(String code) {
        return authSource.checkCode(code);
    }

    public Task<Void> sendUserResetPasswordEmail(String gmail) {
        return authSource.sendUserResetPasswordEmail(gmail);
    }

    public PublishSubject<Boolean> changePassword(String oldPassword, String newPassword) {
        return authSource.changePassword(oldPassword, newPassword);
    }

    public void logout() {
        authSource.logout();
    }
}
