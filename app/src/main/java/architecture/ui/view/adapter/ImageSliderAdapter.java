package architecture.ui.view.adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;
import architecture.ui.view.fragment.SliderItemFragment;

public class ImageSliderAdapter extends FragmentStateAdapter {

    private final List<String> imageUrls;

    public ImageSliderAdapter(@NonNull Fragment fragment, List<String> imageUrls) {
        super(fragment);
        this.imageUrls = imageUrls;
    }


    private Bundle createBundle(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("image_url", url);
        return bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        SliderItemFragment fragment = new SliderItemFragment();
        fragment.setArguments(createBundle(imageUrls.get(position)));
        return fragment;
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
