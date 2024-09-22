package architecture.ui.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.DetailMovieItemBinding;
import architecture.data.local.entity.Movie;
import architecture.ui.view.other.MovieOnClickCallback;

public class MovieListAdapter extends PagingDataAdapter<Movie, MovieListAdapter.MovieDetailViewHolder> {
    private final Context context;
    private final MovieOnClickCallback callback;

    public MovieListAdapter(Context context, MovieOnClickCallback callback, @NonNull DiffUtil.ItemCallback<Movie> diffCallback) {
        super(diffCallback);
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MovieDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DetailMovieItemBinding binding = DetailMovieItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MovieDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDetailViewHolder holder, int position) {
        Movie movie = getItem(position);
        if(movie == null) {
            return;
        }
        holder.binding.setMovie(movie);
        holder.binding.getRoot().setOnClickListener(view -> callback.onClick(movie.id, movie.movieId));
    }

    public static class MovieDetailViewHolder extends RecyclerView.ViewHolder {

        private final DetailMovieItemBinding binding;

        public MovieDetailViewHolder(@NonNull DetailMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
