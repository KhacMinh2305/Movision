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
import com.example.movision.databinding.HomePeopleItemBinding;

import java.util.List;

import architecture.data.local.entity.People;
import architecture.ui.view.other.PeopleItemOnClickCallback;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleItemViewHolder> {
    private Context context;
    private int layout;
    private final PeopleItemOnClickCallback callback;
    private final AsyncListDiffer<People> mAsyncListDiffer;

    public PeopleAdapter(Context context, int layout, PeopleItemOnClickCallback callback) {
        this.context = context;
        this.layout = layout;
        this.callback = callback;
        DiffUtil.ItemCallback<People> diffUtilCallback = new DiffUtil.ItemCallback<People>() {
            @Override
            public boolean areItemsTheSame(@NonNull People oldItem, @NonNull People newItem) {
                return oldItem.id == newItem.id;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull People oldItem, @NonNull People newItem) {
                return oldItem.equals(newItem);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, diffUtilCallback);
    }

    @NonNull
    @Override
    public PeopleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomePeopleItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), layout, parent, false);
        return new PeopleItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleItemViewHolder holder, int position) {
        if(mAsyncListDiffer.getCurrentList().isEmpty()) {
            return;
        }
        People people = mAsyncListDiffer.getCurrentList().get(position);
        holder.binding.setPeople(people);
        holder.binding.getRoot().setOnClickListener(view -> {
            callback.onClick(people.id);
        });
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<People> people) {
        mAsyncListDiffer.submitList(people);
    }

    public static class PeopleItemViewHolder extends RecyclerView.ViewHolder {

        private final HomePeopleItemBinding binding;

        public PeopleItemViewHolder(@NonNull HomePeopleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
