package architecture.ui.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.databinding.MovieClipItemBinding;
import java.util.List;
import architecture.data.model.movie.in_app.ClipUrl;

public class MovieClipAdapter extends RecyclerView.Adapter<MovieClipAdapter.MovieClipViewHolder> {

    private Context context;
    private List<ClipUrl> listData;

    public List<ClipUrl> getListData() { return listData; }

    public MovieClipAdapter(Context context, List<ClipUrl> listData) {
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public MovieClipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieClipItemBinding binding = MovieClipItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MovieClipViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieClipViewHolder holder, int position) {
        holder.bind(listData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class MovieClipViewHolder extends RecyclerView.ViewHolder {
        private MovieClipItemBinding binding;

        public void bind(String clipTitle) {
            binding.clipTitleTextView.setText(clipTitle);
        }

        public MovieClipViewHolder(@NonNull MovieClipItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
