package architecture.ui.view.other;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import architecture.data.model.movie.in_app.MovieItem;

public class SearchingIItemMovieComparator extends DiffUtil.ItemCallback<MovieItem> {


    @Override
    public boolean areItemsTheSame(@NonNull MovieItem oldItem, @NonNull MovieItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull MovieItem oldItem, @NonNull MovieItem newItem) {
        return oldItem.equals(newItem);
    }
}
