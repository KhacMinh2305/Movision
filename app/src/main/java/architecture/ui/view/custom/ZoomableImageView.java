package architecture.ui.view.custom;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.movision.R;

public class ZoomableImageView extends FrameLayout {

    ObjectAnimator animator;

    public ZoomableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.zoomable_image_view, this);
        this.addView(view);
    }

}
