package architecture.ui.view.custom;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.movision.R;
import architecture.ui.view.other.OnClipChangeCallback;

public class SnapRecyclerView extends RecyclerView {

    private SnapHelper snapHelper;
    private LayoutManager layoutManager;
    private TextView currTextView;
    private OnClipChangeCallback callback;
    private int index = -1;

    public int getSnappedIndex() {
        return index;
    }

    public void syncSnappedIndex(int index) {
        this.index = index;
    }

    public SnapRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    public void initOnBindData(OnClipChangeCallback callback) {
        this.callback = callback;
    }

    private void updateUi(View view) {
        if(view == null) {
            return;
        }
        currTextView = view.findViewById(R.id.clipTitleTextView);
        currTextView.setTextColor(getContext().getColor(R.color.widget));
    }

    private void clearOnChangeSnappedItem() {
        currTextView.setTextColor(getContext().getColor(R.color.white));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View snappedView = snapHelper.findSnapView(layoutManager);
        if(snappedView == null) {
            return;
        }
        if(index == -1) {
            updateUi(snappedView);
            index = 0;
            callback.onClipChange(index);
            return;
        }
        int currIndex = layoutManager.getPosition(snappedView);
        if(currIndex != index) {
            clearOnChangeSnappedItem();
            index = currIndex;
            updateUi(snappedView);
            callback.onClipChange(index);
        }
    }
}

// lam sao de lay duoc data tu trong adapter ra
// lam sao de lay nhan duoc thong tin scroll change cua recyclerview