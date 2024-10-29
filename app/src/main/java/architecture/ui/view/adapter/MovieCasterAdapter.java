package architecture.ui.view.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.MovieCasterItemBinding;
import java.util.List;
import java.util.Objects;
import architecture.data.model.people.Caster;
import architecture.ui.view.other.PeopleItemOnClickCallback;

public class MovieCasterAdapter extends RecyclerView.Adapter<MovieCasterAdapter.MovieCasterViewHolder> {

    private Context context;
    private PeopleItemOnClickCallback callback;
    private AsyncListDiffer<Caster> mAsyncListDiffer;

    public MovieCasterAdapter(Context context, PeopleItemOnClickCallback callback) {
        this.context = context;
        this.callback = callback;
        DiffUtil.ItemCallback<Caster> diffCallback = new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull Caster oldItem, @NonNull Caster newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Caster oldItem, @NonNull Caster newItem) {
                return oldItem.equals(newItem);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public MovieCasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieCasterItemBinding binding = MovieCasterItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MovieCasterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCasterViewHolder holder, int position) {
        Caster caster = mAsyncListDiffer.getCurrentList().get(position);
        if(caster == null) return;
        holder.bind(caster);
        holder.itemView.setOnClickListener(view -> callback.onClick(caster.getId()));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submit(List<Caster> casters) {
        mAsyncListDiffer.submitList(casters);
    }

    public static class MovieCasterViewHolder extends RecyclerView.ViewHolder {

        private MovieCasterItemBinding binding;

        public void bind(Caster caster) {
            binding.setCaster(caster);
        }

        public MovieCasterViewHolder(@NonNull MovieCasterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
