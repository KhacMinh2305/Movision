package architecture.data.source;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import architecture.data.source.other.CredentialsProvider;
import architecture.data.source.other.FacebookAuthCallback;
import architecture.data.source.other.FacebookAuthCallbackImpl;
import architecture.other.AppConstant;
import architecture.other.AppMessage;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class AuthenticationDataSource {
    private final FirebaseAuth authenticator;
    private final CredentialsProvider credentialsProvider;
    private FirebaseUser currentUser; // for new features in the future , create a list of Users
    private final FacebookAuthCallback facebookAuthCallback;
    private String verificationCode = "";

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public String getUserUid() {
        return (currentUser != null) ? currentUser.getUid() : null;
    }

    @Inject
    public AuthenticationDataSource(FirebaseAuth authenticator, CredentialsProvider credentialsProvider, FacebookAuthCallback facebookAuthCallback) {
        this.authenticator = authenticator;
        this.credentialsProvider = credentialsProvider;
        this.facebookAuthCallback = facebookAuthCallback;
    }

    public void updateUser() {
        currentUser = null;
        currentUser = authenticator.getCurrentUser();
    }

    public Task<Void> syncUserChanges() {
        return currentUser.reload();
    }

    public boolean checkIfSignedIn() {
        currentUser = authenticator.getCurrentUser();
        return currentUser != null;
    }

    public Task<AuthResult> signInUserWithGmailAndPassword(String gmail, String password) {
        return authenticator.signInWithEmailAndPassword(gmail, password);
    }

    public Task<AuthResult> signUpWithEmailAndPassword(String email, String password) {
        return authenticator.createUserWithEmailAndPassword(email, password);
    }

    public PublishSubject<Boolean> getGoogleLoginState(Context activityContext) {
        PublishSubject<Boolean> federatedLoginSuccess = PublishSubject.create();
        credentialsProvider.requestGoogleCredential(activityContext, new CredentialManagerCallback<>() {
            @Override
            public void onResult(GetCredentialResponse getCredentialResponse) {
                Credential credential = getCredentialResponse.getCredential(); // move to Provider if refactor
                GoogleIdTokenCredential googleIdCredential = GoogleIdTokenCredential
                        .createFrom(credential.getData());
                String googleIdToken = googleIdCredential.getIdToken();
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null);
                authenticator.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        currentUser = task.getResult().getUser();
                        federatedLoginSuccess.onNext(true);
                    }
                });
            }

            @Override
            public void onError(@NonNull GetCredentialException e) {
                federatedLoginSuccess.onNext(false);
            }
        });
        return federatedLoginSuccess;
    }

    public void getResultFromFacebookTokenRequest(int requestCode, int resultCode, @Nullable Intent data) {
        facebookAuthCallback.getResult(requestCode, resultCode, data);
    }

    public PublishSubject<String> getFacebookLoginState(Fragment fragment) {
        PublishSubject<String> federatedLoginSuccess = PublishSubject.create();
        facebookAuthCallback.init(fragment, new FacebookAuthCallbackImpl.OnResultCallback() {
            @Override
            public void onSuccess(AccessToken accessToken) {
                AuthCredential facebookCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
                authenticator.signInWithCredential(facebookCredential).addOnSuccessListener(authResult -> {
                    currentUser = authResult.getUser();
                    federatedLoginSuccess.onNext(AppMessage.SIGN_IN_FB_RESULT_SUCCESS);
                }).addOnFailureListener(e -> federatedLoginSuccess.onNext(Objects.requireNonNull(e.getMessage())));
            }

            @Override
            public void onCancel() {
                federatedLoginSuccess.onNext(AppMessage.SIGN_IN_FB_ACTION_CANCEL);
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                federatedLoginSuccess.onNext(Objects.requireNonNull(e.getMessage()));
            }
        });
        return federatedLoginSuccess;
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put(AppConstant.GMAIL_AUTH_PROPS, AppConstant.GMAIL_AUTH_VALUE);
        props.put(AppConstant.GMAIL_HOST_PROPS, AppConstant.GMAIL_HOST_VALUE);
        props.put(AppConstant.GMAIL_PORT_PROPS, AppConstant.GMAIL_PORT_VALUE);
        props.put(AppConstant.GMAIL_SSL_PROPS, AppConstant.GMAIL_SSL_VALUE);
        return Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(AppConstant.GMAIL_APP_SENDER, AppConstant.GMAIL_PASSWORD);
            }
        });
    }

    private void generateVerificationCode() {
        int code = 0;
        while (code < 99999) {
            code = (int) (Math.random() * 1000000);
        }
        verificationCode = String.valueOf(code);
    }

    private MimeMessage createMessage(Session session, String userGmail) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userGmail));
            message.setSubject(AppMessage.GMAIL_VERIFICATION_TITLE);
            generateVerificationCode();
            message.setText(AppMessage.GMAIL_VERIFICATION_CONTENT + verificationCode);
        } catch (MessagingException e) {
            return null;
        }
        return message;
    }

    @SuppressLint("CheckResult")
    public Single<Boolean> sendVerificationCodeToEmail(String userGmail) {
        return Single.fromCallable(() -> {
            Session session = createSession();
            try {
                MimeMessage message = createMessage(session, userGmail);
                if(message == null) {
                    return false;
                }
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                return false;
            }
        }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread());
    }

    public boolean checkCode(String code) {
        return verificationCode.equals(code);
    }

    public Task<Void> sendUserResetPasswordEmail(String gmail) {
        return authenticator.sendPasswordResetEmail(gmail);
    }

    private void setOneShotResult(PublishSubject<Boolean> result, Boolean value) {
        result.onNext(value);
        result.onComplete();
    }

    public PublishSubject<Boolean> changePassword(String oldPassword, String newPassword) {
        PublishSubject<Boolean> resultPublisher = PublishSubject.create();
        AuthCredential authCredential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(currentUser.getEmail()), oldPassword);
        currentUser.reauthenticate(authCredential).addOnSuccessListener(unused ->
                changePasswordIfValid(newPassword, resultPublisher))
                .addOnFailureListener(e -> setOneShotResult(resultPublisher, false));
        return resultPublisher;
    }

    private void changePasswordIfValid(String newPassword, PublishSubject<Boolean> resultPublisher) {
        currentUser.updatePassword(newPassword).addOnSuccessListener(task ->
                setOneShotResult(resultPublisher, true))
                .addOnFailureListener(e -> setOneShotResult(resultPublisher, false));
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        currentUser = null;
    }
}
