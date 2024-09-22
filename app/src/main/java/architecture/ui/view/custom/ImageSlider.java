package architecture.ui.view.custom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import com.example.movision.R;
import com.example.movision.databinding.ImageSliderBinding;
import org.reactivestreams.Subscription;
import java.util.List;
import java.util.concurrent.TimeUnit;

import architecture.ui.view.adapter.ImageSliderAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImageSlider extends FrameLayout implements DefaultLifecycleObserver {

    private ImageSliderBinding binding;
    private final Flowable<Long> timer = Flowable.interval(3, TimeUnit.SECONDS);
    private int currentIndex = 0;
    private int count  = 0;
    public Subscription subscription;
    private final ViewPager2.OnPageChangeCallback pageChangedCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;
            binding.indicator.onSlide(currentIndex);
        }
    };

    public ImageSliderBinding getBinding() {
        return binding;
    }

    public ImageSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.image_slider, this, false);
        addView(binding.getRoot());
        // add animation
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);
        binding.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.7f + r * 0.3f);
        });
        binding.viewPager.setPageTransformer(transformer);
    }

    public void setListUrls(Fragment parentFragment, List<String> urls) {
        ImageSliderAdapter adapter = new ImageSliderAdapter(parentFragment, urls);
        count = urls.size();
        binding.viewPager.setAdapter(adapter);
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @SuppressLint("CheckResult")
    public void startSliding() {
        binding.viewPager.registerOnPageChangeCallback(pageChangedCallback);
        binding.indicator.onSlide(0);
        timer.subscribeOn(Schedulers.single())
                .doOnSubscribe(subscription -> this.subscription = subscription)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
                    currentIndex = (currentIndex == count - 1) ? -1 : currentIndex;
                    currentIndex++;
                    binding.viewPager.setCurrentItem(currentIndex, true);
                    binding.indicator.onSlide(currentIndex);
                }, throwable -> {});
    }

    public void stopSliding() {
        if(subscription != null) {
            subscription.cancel();
            binding.viewPager.unregisterOnPageChangeCallback(pageChangedCallback);
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        startSliding();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
        stopSliding();
    }
}
