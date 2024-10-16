package architecture.ui.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.PeopleListItemBinding;
import architecture.data.local.entity.People;
import architecture.ui.view.other.PeopleItemOnClickCallback;

public class PeopleListAdapter extends PagingDataAdapter<People, PeopleListAdapter.PeopleListItemViewHolder> {

    private final Context context;
    private final PeopleItemOnClickCallback callback;

    public PeopleListAdapter(@NonNull DiffUtil.ItemCallback<People> diffCallback, Context context, PeopleItemOnClickCallback callback) {
        super(diffCallback);
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PeopleListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PeopleListItemBinding binding = PeopleListItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new PeopleListItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleListItemViewHolder holder, int position) {
        People people = getItem(position);
        if(people == null) {
            return;
        }
        holder.binding.setPeople(people);
        holder.binding.getRoot().setOnClickListener(view -> callback.onClick(people.id));
    }

    public static class PeopleListItemViewHolder extends RecyclerView.ViewHolder{

        PeopleListItemBinding binding;

        public PeopleListItemViewHolder(@NonNull PeopleListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
