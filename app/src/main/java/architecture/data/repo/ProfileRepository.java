package architecture.data.repo;

import javax.inject.Inject;

import architecture.data.source.ProfileDataSource;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ProfileRepository {

    private final ProfileDataSource profileSource;

    @Inject
    public ProfileRepository(ProfileDataSource profileSource) {
        this.profileSource = profileSource;
    }

    public String getUserUid() {
        return profileSource.getUserUid();
    }

    public PublishSubject<Boolean> updateUserProfile(String name, String imageUrl) {
        return profileSource.updateUserProfile(name, imageUrl);
    }
}
