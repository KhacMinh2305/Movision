package architecture.ui.view.custom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import com.example.movision.R;

public class ImageSliderIndicator extends TableRow {

    private static final int MAX_COUNT = 10;
    private static final int DEFAULT_INDICATOR_SIZE = 18;
    private static final int DEFAULT_INDICATOR_MARGIN = 10;
    private static final int INDICATOR_CHOSEN_SIZE = 36;
    private int count;
    private ImageView currentIndicator;
    public final TableRow.LayoutParams indicatorParams =
            new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    private final TableRow.LayoutParams chosenIndicatorParams =
            new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);

    public ImageSliderIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageSlidingIndicator, 0, 0);
        try {
            count = typedArray.getInt(R.styleable.ImageSlidingIndicator_indicatorCount, 0);
        } finally {
            typedArray.recycle();
        }
        addIndicators();
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void addIndicators() {
        if (count == 0) {
            return;
        }
        count = Math.min(count, MAX_COUNT);
        indicatorParams.weight = count;
        indicatorParams.width = DEFAULT_INDICATOR_SIZE;
        indicatorParams.height = DEFAULT_INDICATOR_SIZE;
        indicatorParams.rightMargin = DEFAULT_INDICATOR_MARGIN;
        chosenIndicatorParams.weight = count;
        chosenIndicatorParams.width = INDICATOR_CHOSEN_SIZE;
        chosenIndicatorParams.height = DEFAULT_INDICATOR_SIZE;
        chosenIndicatorParams.rightMargin = DEFAULT_INDICATOR_MARGIN;

        for (int i = 0; i < count; i++) {
            currentIndicator = new ImageView(this.getContext(), null);
            currentIndicator.setLayoutParams(indicatorParams);
            currentIndicator.setScaleType(ImageView.ScaleType.CENTER_CROP);
            currentIndicator.setBackground(getResources().getDrawable(R.drawable.image_slider_indicator, null));
            this.addView(currentIndicator);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onSlide(int newPosition) {
        if(currentIndicator == null) {
            return;
        }
        // decrease params
        currentIndicator.setLayoutParams(indicatorParams);
        currentIndicator.setBackground(getResources().getDrawable(R.drawable.image_slider_indicator, null));
        currentIndicator = (ImageView) this.getChildAt(newPosition);
        // increase params
        currentIndicator.setLayoutParams(chosenIndicatorParams);
        currentIndicator.setBackground(getResources().getDrawable(R.drawable.image_slider_indicator_chosen, null));
    }
}
