package architecture.ui.view.adapter;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.SearchQueryItemBinding;
import java.util.List;
import architecture.data.local.entity.SearchQuery;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.HistoryViewHolder> {

    public interface SearchQueryItemListener {
        void onDelete(long id);
        void onClick(String query);
    }

    private final SearchQueryItemListener listener;
    private final AsyncListDiffer<SearchQuery> mAsyncListDiffer;

    public HistorySearchAdapter(SearchQueryItemListener listener) {
        this.listener = listener;
        DiffUtil.ItemCallback<SearchQuery> callback = new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull SearchQuery oldItem, @NonNull SearchQuery newItem) {
                return oldItem.id == newItem.id;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SearchQuery oldItem, @NonNull SearchQuery newItem) {
                return oldItem.equals(newItem);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, callback);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchQueryItemBinding binding = SearchQueryItemBinding.
                inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchQuery searchQuery = mAsyncListDiffer.getCurrentList().get(position);
        if(searchQuery == null) return;
        holder.bind(searchQuery);
        holder.binding.queryTextView.setOnClickListener(v -> listener.onClick(searchQuery.query));
        holder.binding.deleteImageButton.setOnClickListener(v -> listener.onDelete(searchQuery.id));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submit(List<SearchQuery> list) {
        mAsyncListDiffer.submitList(list);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final SearchQueryItemBinding binding;

        public void bind(SearchQuery searchQuery) {
            binding.setQuery(searchQuery);
        }

        public HistoryViewHolder(@NonNull SearchQueryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}