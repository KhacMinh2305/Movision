package architecture.data.source.other;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import architecture.data.model.movie.in_app.MovieReview;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MovieReviewSource extends RxPagingSource<Long, MovieReview> {

    public static interface AddingCallback {
        void onReceivedNewReview(MovieReview review);
    }

    private final FirebaseFirestore cloud;
    private final int movieId;
    private final int pageSize;
    private final List<MovieReview> addedList;
    private final AddingCallback callback;

    public MovieReviewSource(FirebaseFirestore cloud, int movieId, int pageSize) {
        this.cloud = cloud;
        this.movieId = movieId;
        this.pageSize = pageSize;
        this.addedList = new ArrayList<>();
        callback = review -> {
            Log.d("Debug", "add thanh cong " + review.getId());
            addedList.add(review);
        };
    }

    public AddingCallback getAddingCallback () {
        return callback;
    }

    private Single<List<MovieReview>> loadItems(Long timeCursor) {
        Task<QuerySnapshot> task = cloud.collection("movie_review").document(String.valueOf(movieId))
                .collection("records").orderBy("created_time")
                .whereGreaterThan("created_time", timeCursor).limit(pageSize).get();
        return Single.fromCallable(() -> Tasks.await(task)).subscribeOn(Schedulers.single()).map(documentSnapshots -> {
            List<MovieReview> results = new ArrayList<>();
            documentSnapshots.forEach(docSnapshot -> results.add(docSnapshot.toObject(MovieReview.class)));
            return results;
        }).subscribeOn(Schedulers.computation());
    }

    @NonNull
    @Override
    public Single<LoadResult<Long, MovieReview>> loadSingle(@NonNull LoadParams<Long> loadParams) {
        Long key = loadParams.getKey();
        if(key == null) {
            key = 0L;
        }
        return loadItems(key).subscribeOn(Schedulers.io()).map(this::toResult);
    }

    private int checkPosition(MovieReview review) {
        int left = 0, right = addedList.size() - 1, mid;
        long targetTime = review.getCreated_time();
        while(left < right - 1) {
            mid = (left + right) / 2;
            if(targetTime == addedList.get(mid).getCreated_time()) return mid;
            left = (addedList.get(mid).getCreated_time() < targetTime) ? mid + 1 : left;
            right = (addedList.get(mid).getCreated_time() > targetTime) ? mid - 1 : right;
        }
        if(targetTime < addedList.get(left).getCreated_time()) return left - 1;
        if(targetTime < addedList.get(right).getCreated_time()) return left;
        return right;
    }

    private void removeCollapsedReviews(List<MovieReview> result) {
        if(addedList.isEmpty() || result.isEmpty()) return;
        MovieReview lastResultItem = result.get(result.size() - 1);
        int index = checkPosition(lastResultItem);
        int maxRemoved = Math.min(result.size() - 1, addedList.size() - 1);
        index = Math.min(index, maxRemoved);
        while (index >= 0) {
            addedList.remove(0);
            result.remove(result.size() - 1);
            index--;
        }
    }

    private LoadResult<Long, MovieReview> toResult(List<MovieReview> result) {
        long nextKey = -1L;
        if(!result.isEmpty() || result.size() == pageSize) {
            nextKey = result.get(result.size() - 1).getCreated_time();
        }
        //removeCollapsedReviews(result);
        return new LoadResult.Page<>(result, null, (nextKey > 0) ? nextKey : null,
                LoadResult.Page.COUNT_UNDEFINED, LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Long getRefreshKey(@NonNull PagingState<Long, MovieReview> pagingState) {
        return null;
    }
}

//TODO: Sau nay sua lai , bien no thanh dang cache nhu ben People