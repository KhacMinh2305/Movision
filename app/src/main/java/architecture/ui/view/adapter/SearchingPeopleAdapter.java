package architecture.ui.view.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.SearchPeopleItemBinding;
import architecture.data.model.people.PeopleItem;
import architecture.ui.view.other.PeopleItemOnClickCallback;

public class SearchingPeopleAdapter extends PagingDataAdapter<PeopleItem, SearchingPeopleAdapter.SearchingPeopleViewHolder> {

    private final PeopleItemOnClickCallback callback;

    public SearchingPeopleAdapter(@NonNull DiffUtil.ItemCallback<PeopleItem> diffCallback,
                                  PeopleItemOnClickCallback callback) {
        super(diffCallback);
        this.callback = callback;
    }

    @NonNull
    @Override
    public SearchingPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchPeopleItemBinding binding = SearchPeopleItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new SearchingPeopleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingPeopleViewHolder holder, int position) {
        PeopleItem item = getItem(position);
        if(item == null) return;
        holder.bind(item);
        holder.itemView.setOnClickListener(view -> callback.onClick(item.getId()));
    }

    public static class SearchingPeopleViewHolder extends RecyclerView.ViewHolder {
        private final SearchPeopleItemBinding binding;

        public void bind(PeopleItem item) {
            binding.setItem(item);
        }

        public SearchingPeopleViewHolder(@NonNull SearchPeopleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
