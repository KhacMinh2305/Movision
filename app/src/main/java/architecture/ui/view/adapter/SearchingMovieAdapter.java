package architecture.ui.view.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.SearchMovieItemBinding;
import architecture.data.model.movie.in_app.MovieItem;
import architecture.ui.view.other.OnClickCallback;

public class SearchingMovieAdapter extends PagingDataAdapter<MovieItem, SearchingMovieAdapter.MovieSearchItemViewHolder> {

    private final OnClickCallback callback;

    public SearchingMovieAdapter(@NonNull DiffUtil.ItemCallback<MovieItem> diffCallback, OnClickCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public MovieSearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchMovieItemBinding binding = SearchMovieItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MovieSearchItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchItemViewHolder holder, int position) {
        MovieItem item = getItem(position);
        if(item == null) return;
        holder.bind(item);
        holder.itemView.setOnClickListener(view -> callback.onClick(item.getId()));
    }

    public static class MovieSearchItemViewHolder extends RecyclerView.ViewHolder {
        private SearchMovieItemBinding binding;

        public void bind(MovieItem item) {
            binding.setItem(item);
        }

        public MovieSearchItemViewHolder(SearchMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
