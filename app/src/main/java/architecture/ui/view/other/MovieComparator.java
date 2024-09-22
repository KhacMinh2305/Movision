package architecture.ui.view.other;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import architecture.data.local.entity.Movie;

public class MovieComparator extends DiffUtil.ItemCallback<Movie>{

    @Override
    public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
        return oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
        return newItem.equals(oldItem);
    }
}
