package architecture.data.source;
import android.net.Uri;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class ProfileDataSource {

    private final AuthenticationDataSource authSource;

    @Inject
    public ProfileDataSource(AuthenticationDataSource authSource) {
        this.authSource = authSource;
    }

    public String getUserUid() {
        return authSource.getUserUid();
    }

    public String getUserAvatarUrl() {
        Uri uri = authSource.getCurrentUser().getPhotoUrl();
        return (uri != null) ? uri.toString() : null;
    }

    public Map<String, String> getUserData() {
        if(authSource.getCurrentUser() == null) return null;
        String name = authSource.getCurrentUser().getDisplayName();
        String gmail = authSource.getCurrentUser().getEmail();
        String phoneNumber = authSource.getCurrentUser().getPhoneNumber();
        name = (name == null || name.isEmpty()) ? AppConstant.UNDEFINED_FIELD : name;
        gmail = (gmail == null || gmail.isEmpty()) ? AppConstant.UNDEFINED_FIELD : gmail;
        phoneNumber = (phoneNumber == null || phoneNumber.isEmpty()) ? AppConstant.UNDEFINED_FIELD : phoneNumber;
        return Map.of(
                "name", name,
                "gmail", gmail,
                "phoneNumber", phoneNumber
        );
    }

    public PublishSubject<Boolean> updateUserProfile(String name, String imageUrl) {
        PublishSubject<Boolean> resultPublisher = PublishSubject.create();
        UserProfileChangeRequest.Builder requestBuilder = new UserProfileChangeRequest.Builder();
        if(name != null) {
            requestBuilder.setDisplayName(name);
        }
        if(imageUrl != null) {
            requestBuilder.setPhotoUri(Uri.parse(imageUrl));
        }
        UserProfileChangeRequest request = requestBuilder.build();
        authSource.getCurrentUser().updateProfile(request).addOnCompleteListener(task -> {
            resultPublisher.onNext(task.isSuccessful());
            resultPublisher.onComplete();
        });
        return resultPublisher;
    }
}
