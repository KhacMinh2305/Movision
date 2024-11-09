package architecture.ui.view.custom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;

public class NonTouchableChip extends Chip {

    public NonTouchableChip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(isChecked()) return false;
        return super.onTouchEvent(event);
    }
}
