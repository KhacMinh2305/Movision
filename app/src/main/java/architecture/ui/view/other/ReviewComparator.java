package architecture.ui.view.other;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import architecture.data.model.movie.in_app.MovieReview;

public class ReviewComparator extends DiffUtil.ItemCallback<MovieReview> {

    @Override
    public boolean areItemsTheSame(@NonNull MovieReview oldItem, @NonNull MovieReview newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull MovieReview oldItem, @NonNull MovieReview newItem) {
        return oldItem.equals(newItem);
    }
}
