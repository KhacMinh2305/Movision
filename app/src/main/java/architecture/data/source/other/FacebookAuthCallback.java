package architecture.data.source.other;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface FacebookAuthCallback {
    void init(Fragment fragment, FacebookAuthCallbackImpl.OnResultCallback callback);
    void getResult(int requestCode, int resultCode, @Nullable Intent data);
}
