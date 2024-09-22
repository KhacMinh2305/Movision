package architecture.ui.view.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.R;
import java.util.List;
import java.util.Objects;
import architecture.data.model.genre.Genre;
import architecture.ui.view.other.GenreOnClickCallback;

public class HomeGenreAdapter extends RecyclerView.Adapter<HomeGenreAdapter.UserGenreViewHolder> {

    private final Context context;
    private final int layout;
    private final AsyncListDiffer<Genre> mAsyncListDiffer;
    private final GenreOnClickCallback genreOnClickCallback;

    public HomeGenreAdapter(Context context, int layout, GenreOnClickCallback genreOnClickCallback) {
        this.context = context;
        this.layout = layout;
        this.genreOnClickCallback = genreOnClickCallback;
        DiffUtil.ItemCallback<Genre> diffUtilCallback = new DiffUtil.ItemCallback<Genre>() {
            @Override
            public boolean areItemsTheSame(@NonNull Genre newGenre, @NonNull Genre oldGenre) {
                return Objects.equals(newGenre.getId(), oldGenre.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Genre newGenre, @NonNull Genre oldGenre) {
                return newGenre.equals(oldGenre);
            }
        };
        mAsyncListDiffer = new AsyncListDiffer<>(this, diffUtilCallback);
    }

    @Override
    public int getItemViewType(int position) {
        return (layout == R.layout.user_genre_item) ? 0 : 1;
    }

    @NonNull
    @Override
    public UserGenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new UserGenreViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGenreViewHolder holder, int position) {
        if(mAsyncListDiffer.getCurrentList().isEmpty()) {
            return;
        }
        Genre genre = mAsyncListDiffer.getCurrentList().get(position);
        holder.bind(genre.getName());
        holder.itemView.setOnClickListener(v -> genreOnClickCallback.onCLick(genre));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Genre> listGenres) {
        mAsyncListDiffer.submitList(listGenres);
    }

    public static class UserGenreViewHolder extends RecyclerView.ViewHolder {

        private final TextView genreNameTextView;

        public void bind(String genreName) {
            if(genreName.isEmpty()) {
                return;
            }
            genreNameTextView.setText(genreName);
        }

        public UserGenreViewHolder(@NonNull View itemView, int layoutType) {
            super(itemView);
            genreNameTextView = (layoutType == 0) ? itemView.findViewById(R.id.genreNameTextView)
                    : itemView.findViewById(R.id.genreTextView);
        }
    }
}
