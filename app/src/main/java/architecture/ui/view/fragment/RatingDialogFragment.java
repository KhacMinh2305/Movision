package architecture.ui.view.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.movision.R;
import architecture.ui.view.custom.CustomRatingBar;
import architecture.ui.view.other.OnRatingListener;

public class RatingDialogFragment extends DialogFragment {
    public static final String TAG = "RATING_DIALOG_FRAGMENT";

    private ImageButton closeButton;
    private CustomRatingBar ratingBar;
    private Button confirmButton;
    private OnRatingListener listener;
    private double currentRating = -1L;

    public static RatingDialogFragment newInstance(OnRatingListener listener) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rating_dialog, container, false);
        init(view);
        setUpEvent();
        return view;
    }

    private void init(View view) {
        closeButton = view.findViewById(R.id.backImageButton);
        ratingBar = view.findViewById(R.id.ratingBar);
        confirmButton = view.findViewById(R.id.confirmButton);
    }

    private void setUpEvent() {
        closeButton.setOnClickListener(v -> dismiss());
        ratingBar.setOnRatingListener(rating -> currentRating = rating);
        confirmButton.setOnClickListener(view -> {
            listener.onRating(currentRating);
            this.dismiss();
        });
    }
}
