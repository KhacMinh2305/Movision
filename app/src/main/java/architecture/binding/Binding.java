package architecture.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movision.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.List;

import architecture.data.model.genre.Genre;
import architecture.other.AppConstant;
import architecture.ui.view.adapter.GenreInsideMovieAdapter;
import architecture.ui.view.other.RecyclerViewItemDecoration;

public class Binding {

    @BindingAdapter("defaultDrawable")
    public static void bindImage(ImageView view, Drawable drawable) {
        if(drawable != null) {
            Glide.with(view.getContext()).load(drawable).into(view);
        }
    }

    @BindingAdapter("imageUrl")
    public static void bindMovieImageToItem(ShapeableImageView imageView, String imageUrl) {
        if (!imageUrl.isEmpty()) {
            Glide.with(imageView.getContext()).load(AppConstant.TMDB_IMAGE_HOST + imageUrl).into(imageView);
            return;
        }
        imageView.setImageResource(R.drawable.user_image);
    }

    @BindingAdapter("movieName")
    public static void bindMovieName(TextView textView, String movieName) {
        if(!movieName.isEmpty()) {
            textView.setText(movieName);
        }
    }

    @BindingAdapter("movieDescription")
    public static void bindMovieDescription(TextView textView, String movieDescription) {
        if(!movieDescription.isEmpty()) {
            textView.setText(movieDescription);
        }
    }

    @BindingAdapter("movieReleaseDate")
    public static void bindMovieReleaseDate(TextView textView, String movieReleaseDate) {
        if(!movieReleaseDate.isEmpty()) {
            textView.setText(movieReleaseDate);
        }
    }

    @BindingAdapter("movieRating")
    public static void bindMovieRating(TextView textView, float movieRating) {
        if(movieRating != 0) {
            textView.setText(String.valueOf(movieRating));
        }
    }

    @BindingAdapter("genreName")
    public static void bindGenreName(TextView textView, String genreName) {
        if(!genreName.isEmpty()) {
            textView.setText(genreName);
        }
    }

    @BindingAdapter("genres")
    public static void bindMovieGenres(RecyclerView recyclerView, String genres) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(8));
        recyclerView.setLayoutManager(layoutManager);
        String[] genreNames = genres.split(",");
        recyclerView.setAdapter(new GenreInsideMovieAdapter(recyclerView.getContext(), Arrays.asList(genreNames)));
    }
}
