package architecture.data.source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import architecture.other.AppConstant;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class MediaDataSource {

    private final FirebaseStorage storage;
    private StorageReference storageRef;

    @Inject
    public MediaDataSource(FirebaseStorage storage) {
        this.storage = storage;
    }

    private String createImageName() {
        long time = System.currentTimeMillis();
        return String.valueOf(time);
    }

    public PublishSubject<Map<Integer, String>> pushUserAvatarToStorage(String userId, byte[] bytes) {
        PublishSubject<Map<Integer, String>> imagePushingResult = PublishSubject.create();
        Map<Integer, String> map = new HashMap<>();
        String imageName = createImageName();
        storageRef = storage.getReference("avatar").child(userId).child(imageName + ".jpg");
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg").build();
        UploadTask uploadTask = storageRef.putBytes(bytes, metadata);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            map.put(AppConstant.SUCCESS_CODE, uri.toString());
                            imagePushingResult.onNext(map);
                            imagePushingResult.onComplete();
                        })
                        .addOnFailureListener(e -> {
                            map.put(AppConstant.FAILURE_CODE, e.getMessage());
                            imagePushingResult.onNext(map);
                            imagePushingResult.onComplete();
                        }))
                .addOnFailureListener(exception -> {
                    map.put(AppConstant.FAILURE_CODE, exception.getMessage());
                    imagePushingResult.onNext(map);
                    imagePushingResult.onComplete();
                });
        return imagePushingResult;
    }
}