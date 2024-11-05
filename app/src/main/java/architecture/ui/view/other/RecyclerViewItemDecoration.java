package architecture.ui.view.other;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Objects;
import architecture.ui.view.adapter.ImageAdapter;
import architecture.ui.view.adapter.MovieAdapter;
import architecture.ui.view.adapter.MovieCasterAdapter;
import architecture.ui.view.adapter.MovieListAdapter;
import architecture.ui.view.adapter.MovieReviewAdapter;
import architecture.ui.view.adapter.PeopleAdapter;
import architecture.ui.view.adapter.PeopleListAdapter;
import architecture.ui.view.adapter.SimilarMovieAdapter;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public RecyclerViewItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = Objects.requireNonNull(parent.getLayoutManager()).getPosition(view);
        if(parent.getAdapter() instanceof MovieAdapter
                || parent.getAdapter() instanceof PeopleAdapter
                || parent.getAdapter() instanceof SimilarMovieAdapter
                || parent.getAdapter() instanceof ImageAdapter) {
            outRect.left = (position == 0) ? 0 : space;
            return;
        }
        if(parent.getAdapter() instanceof MovieListAdapter
                || parent.getAdapter() instanceof MovieReviewAdapter) {
            outRect.top = (position == 0) ? 0 : space;
            return;
        }
        if(parent.getAdapter() instanceof PeopleListAdapter) {
            outRect.top = (position == 0 || position == 1) ? 0 : space;
            return;
        }
        if(parent.getAdapter() instanceof MovieCasterAdapter) {
            outRect.left = (position == 0 || position == 1) ? 0 : space;
            outRect.top = (position % 2 != 0) ? space : 0;
        }
    }
}
