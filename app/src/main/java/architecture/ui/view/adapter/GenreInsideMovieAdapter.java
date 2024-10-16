package architecture.ui.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movision.R;
import com.example.movision.databinding.GenreInsideMovieItemBinding;
import java.util.List;

public class GenreInsideMovieAdapter extends RecyclerView.Adapter<GenreInsideMovieAdapter.InsideGenreViewHolder> {

    private final List<String> genres;

    public GenreInsideMovieAdapter(List<String> genres) {
        this.genres = genres;
    }

    @NonNull
    @Override
    public InsideGenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GenreInsideMovieItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.genre_inside_movie_item, parent, false);
        return new InsideGenreViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InsideGenreViewHolder holder, int position) {
        String name = genres.get(position);
        holder.binding.setName(name);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class InsideGenreViewHolder extends RecyclerView.ViewHolder {

        private final GenreInsideMovieItemBinding binding;

        public InsideGenreViewHolder(@NonNull GenreInsideMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
