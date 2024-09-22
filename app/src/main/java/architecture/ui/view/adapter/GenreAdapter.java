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
import architecture.ui.view.other.ChoseGenresCallback;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private Context context;
    private ChoseGenresCallback callback;
    private final AsyncListDiffer<Genre> mAsyncListDiffer;

    public GenreAdapter(Context context, ChoseGenresCallback callback) {
        this.context = context;
        this.callback = callback;
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

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_genres_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = mAsyncListDiffer.getCurrentList().get(position);
        holder.bind(genre.getName());
        holder.genreTextView.setOnClickListener(view -> {
            boolean chosen = holder.updateViewOnClick();
            if(chosen) {
                callback.chose(genre);
                return;
            }
            callback.unChose(genre);
        });
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<Genre> listGenres) {
        mAsyncListDiffer.submitList(listGenres);
    }


    public static class GenreViewHolder extends RecyclerView.ViewHolder {

        private final TextView genreTextView;
        private boolean isChosen = false;

        public void bind(String genre) {
            genreTextView.setText(genre);
        }

        public boolean updateViewOnClick() {
            isChosen = !isChosen;
            if (isChosen) {
                genreTextView.setBackgroundResource(R.drawable.genres_item_chosen_background);
                return true;
            }
            genreTextView.setBackgroundResource(R.drawable.genre_item_background);
            return false;
        }

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
        }
    }
}
