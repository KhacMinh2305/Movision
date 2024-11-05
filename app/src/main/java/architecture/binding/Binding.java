package architecture.binding;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.example.movision.R;
import com.google.android.material.imageview.ShapeableImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import architecture.other.AppConstant;

public class Binding {

    @BindingAdapter("reviewTime")
    public static void bindReviewTime(TextView textView, String time) {
        if(time == null || time.isEmpty()) return;
        long currentTime = Long.parseLong(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String text = simpleDateFormat.format(date);
        textView.setText(text);
    }

    @BindingAdapter("defaultDrawable")
    public static void bindImage(ImageView view, Drawable drawable) {
        if(drawable != null) {
            Glide.with(view.getContext()).load(drawable).into(view);
        }
    }

    @BindingAdapter({"userAvatar", "defaultAvatar"})
    public static void bindUserAvatar(ShapeableImageView imageView, String userAvatar, Drawable defaultAvatar) {
        if (userAvatar!= null && !userAvatar.isEmpty()) {
            Glide.with(imageView.getContext()).load(userAvatar).into(imageView);
            return;
        }
        Glide.with(imageView.getContext()).load(defaultAvatar).into(imageView);
    }

    @BindingAdapter("imageUrl")
    public static void bindMovieImageToItem(ShapeableImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
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

    //peopleName
    @BindingAdapter("peopleName")
    public static void bindPeopleName(TextView textView, String peopleName) {
        if(!peopleName.isEmpty()) {
            textView.setText(peopleName);
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("duration")
    public static void binDuration(TextView textView, int duration) {
        textView.setText(duration + " minutes");
    }
}
