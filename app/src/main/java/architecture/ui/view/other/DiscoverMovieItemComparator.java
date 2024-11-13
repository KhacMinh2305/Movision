package architecture.ui.view.other;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import architecture.data.model.movie.in_app.DiscoverMovieItem;

public class DiscoverMovieItemComparator extends DiffUtil.ItemCallback<DiscoverMovieItem> {

    @Override
    public boolean areItemsTheSame(@NonNull DiscoverMovieItem oldItem, @NonNull DiscoverMovieItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull DiscoverMovieItem oldItem, @NonNull DiscoverMovieItem newItem) {
        return oldItem.equals(newItem);
    }
}
