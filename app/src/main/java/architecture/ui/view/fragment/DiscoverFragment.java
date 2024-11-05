package architecture.ui.view.fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.databinding.FragmentDiscoverBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import architecture.ui.viewmodel.DiscoverViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DiscoverFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {

    }

    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentDiscoverBinding binding;
    private SharedViewModel sharedViewModel;
    private DiscoverViewModel viewModel;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.discoverSheet);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        setupEventListeners();
    }

    private void setupEventListeners() {
        binding.discoverImageButton.setOnClickListener(view ->
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    sharedViewModel.setBottomNavBarVisibility(true);
                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    sharedViewModel.setBottomNavBarVisibility(false);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }
}