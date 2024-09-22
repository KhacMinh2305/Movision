package architecture.ui.view.other;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Objects;

import architecture.ui.view.adapter.GenreInsideMovieAdapter;
import architecture.ui.view.adapter.MovieAdapter;
import architecture.ui.view.adapter.MovieListAdapter;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public RecyclerViewItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = Objects.requireNonNull(parent.getLayoutManager()).getPosition(view);
        if(parent.getAdapter() instanceof MovieAdapter || parent.getAdapter() instanceof GenreInsideMovieAdapter) {
            outRect.left = (position == 0) ? 0 : space;
            return;
        }
        if(parent.getAdapter() instanceof MovieListAdapter) {
            outRect.top = (position == 0) ? 0 : space;
        }
    }
}
