package architecture.ui.view.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.R;
import com.example.movision.databinding.SimilarMovieItemBinding;
import java.util.List;
import architecture.data.model.movie.in_app.SimilarMovie;
import architecture.ui.view.other.SimilarMovieOnClickCallback;

public class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieViewHolder> {

    private Context context;
    private final AsyncListDiffer<SimilarMovie> mAsyncListDiffer;
    private final SimilarMovieOnClickCallback callback;

    public SimilarMovieAdapter(Context context, SimilarMovieOnClickCallback callback) {
        this.context = context;
        this.callback = callback;
        DiffUtil.ItemCallback<SimilarMovie> diffUtilCallback = new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull SimilarMovie oldItem, @NonNull SimilarMovie newItem) {
                return oldItem.getMovieId() == newItem.getMovieId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SimilarMovie oldItem, @NonNull SimilarMovie newItem) {
                return oldItem.equals(newItem);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, diffUtilCallback);
    }

    @NonNull
    @Override
    public SimilarMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimilarMovieAdapter.SimilarMovieViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.similar_movie_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarMovieViewHolder holder, int position) {
        if(mAsyncListDiffer.getCurrentList().isEmpty()) {
            return;
        }
        SimilarMovie movie = mAsyncListDiffer.getCurrentList().get(position);
        holder.binding.setSimilarMovie(movie);
        holder.binding.getRoot().setOnClickListener(view -> callback.onClick(movie.getMovieId()));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<SimilarMovie> listMovies) {
        mAsyncListDiffer.submitList(listMovies);
    }

    public static class SimilarMovieViewHolder extends RecyclerView.ViewHolder {
        private final SimilarMovieItemBinding binding;

        public SimilarMovieViewHolder(@NonNull SimilarMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
