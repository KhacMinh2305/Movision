package architecture.ui.view.custom;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.example.movision.R;
import java.util.ArrayList;
import java.util.List;
import architecture.ui.view.other.OnRatingListener;

public class CustomRatingBar extends LinearLayout {

    private static final int MAX_STAR = 10;
    private final LinearLayout.LayoutParams params = new LayoutParams(100, 100, 1);
    private List<ImageButton> listRatingButton;

    public CustomRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWeightSum(MAX_STAR);
        setOrientation(HORIZONTAL);
        listRatingButton = new ArrayList<>();
        params.gravity = Gravity.CENTER;
        for(int i = 0; i < MAX_STAR; i++) {
            ImageButton ratingButton = new ImageButton(this.getContext(), null);
            ratingButton.setLayoutParams(params);
            ratingButton.setBackground(null);
            ratingButton.setPadding(20, 20, 20, 20);
            ratingButton.setImageResource(R.drawable.ic_rating);
            ratingButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            this.addView(ratingButton);
            listRatingButton.add(ratingButton);
        }
    }

    private void clearChoice() {
        for(int i = 0; i < listRatingButton.size(); i++) {
            listRatingButton.get(i).setImageResource(R.drawable.ic_rating);
        }
    }

    private void changeUiOnRating(int chosenIndex) {
        clearChoice();
        for(int i = 0; i <= chosenIndex; i++) {
            listRatingButton.get(i).setImageResource(R.drawable.ic_rating_filled);
        }
    }

    public void setOnRatingListener(OnRatingListener listener) {
        for(int i = 0; i < listRatingButton.size(); i++) {
            int index = i;
            listRatingButton.get(i).setOnClickListener(v -> {
                changeUiOnRating(index);
                listener.onRating(index + 1);
            });
        }
    }
}
