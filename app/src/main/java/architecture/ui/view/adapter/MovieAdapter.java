package architecture.ui.view.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.PreviewMovieItemBinding;
import java.util.List;
import architecture.data.local.entity.Movie;
import architecture.ui.view.other.MovieOnClickCallback;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieListItemViewHolder> {

    private Context context;
    private int layout;
    private final AsyncListDiffer<Movie> mAsyncListDiffer;
    private final MovieOnClickCallback callback;

    public MovieAdapter(Context context, int layout, MovieOnClickCallback callback) {
        this.context = context;
        this.layout = layout;
        this.callback = callback;
        DiffUtil.ItemCallback<Movie> diffUtilCallback = new DiffUtil.ItemCallback<Movie>() {
            @Override
            public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                return oldItem.movieId == newItem.movieId;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                return oldItem.equals(newItem);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, diffUtilCallback);
    }

    @NonNull
    @Override
    public MovieListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieListItemViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListItemViewHolder holder, int position) {
        if(mAsyncListDiffer.getCurrentList().isEmpty()) {
            return;
        }
        Movie movie = mAsyncListDiffer.getCurrentList().get(position);
        holder.binding.setMovie(movie);
        holder.binding.getRoot().setOnClickListener(view -> callback.onClick(movie.id, movie.movieId));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Movie> listMovies) {
        mAsyncListDiffer.submitList(listMovies);
    }

    public static class MovieListItemViewHolder extends RecyclerView.ViewHolder {

        private final PreviewMovieItemBinding binding;

        public MovieListItemViewHolder(@NonNull PreviewMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
