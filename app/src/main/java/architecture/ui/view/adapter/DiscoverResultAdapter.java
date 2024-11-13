package architecture.ui.view.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movision.databinding.DiscoverMovieItemBinding;

import architecture.data.model.movie.in_app.DiscoverMovieItem;
import architecture.ui.view.other.OnClickCallback;

public class DiscoverResultAdapter extends PagingDataAdapter<DiscoverMovieItem, DiscoverResultAdapter.DiscoverMovieItemViewHolder> {

    private final OnClickCallback callback;

    public DiscoverResultAdapter(@NonNull DiffUtil.ItemCallback<DiscoverMovieItem> diffCallback,
                                 OnClickCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public DiscoverMovieItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DiscoverMovieItemBinding binding = DiscoverMovieItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DiscoverMovieItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverMovieItemViewHolder holder, int position) {
        DiscoverMovieItem item = getItem(position);
        if(item == null) return;
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> callback.onClick(item.getId()));
    }

    public static class DiscoverMovieItemViewHolder extends RecyclerView.ViewHolder {

        private DiscoverMovieItemBinding binding;

        public void bind(DiscoverMovieItem item) {
            binding.setItem(item);
        }

        public DiscoverMovieItemViewHolder(@NonNull DiscoverMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
