package architecture.ui.view.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.ReviewItemBinding;

import java.util.List;

import architecture.data.model.movie.in_app.MovieReview;

public class MovieReviewAdapter extends PagingDataAdapter<MovieReview, MovieReviewAdapter.ReviewItemViewHolder> {

    public MovieReviewAdapter(@NonNull DiffUtil.ItemCallback<MovieReview> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ReviewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewItemBinding binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewItemViewHolder holder, int position) {
        MovieReview item = getItem(position);
        if(item == null) return;
        holder.bind(item);
    }

    public static class ReviewItemViewHolder extends RecyclerView.ViewHolder {

        private ReviewItemBinding binding;

        public void bind(MovieReview review) {
            binding.setReview(review);
        }

        public ReviewItemViewHolder(@NonNull ReviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
