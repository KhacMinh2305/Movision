package architecture.ui.view.fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.movision.R;
import com.example.movision.databinding.FragmentProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import architecture.ui.viewmodel.ProfileViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentProfileBinding binding;
    private SharedViewModel sharedViewModel;
    private ProfileViewModel viewModel;
    private NavController navController;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if(uri != null) {
                    Log.d("Debug", "Da lay duoc hinh anh : " + uri);
                    return;
                }
                Log.d("Debug", "Pick image failed !");
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        observeStates();
        setupEvents();
    }

    private void init() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
    }

    private void observeStates() {
        sharedViewModel.setBottomNavBarVisibility(false);
        viewModel.getSignOutNavigationState().observe(getViewLifecycleOwner(),
                navigate -> navController.navigate(R.id.logout));
        sharedViewModel.getImageDataState().observe(getViewLifecycleOwner(),
                bitmap -> Glide.with(this).load(bitmap).into(binding.userShapeableImageView));
    }

    private void setupEvents() {
        binding.backImageButton.setOnClickListener(v -> {
            sharedViewModel.setBottomNavBarVisibility(true);
            navController.navigateUp();
        });
        binding.signOutButton.setOnClickListener(v -> viewModel.signOut());
        binding.peekAvatarImageButton.setOnClickListener(view ->
                navController.navigate(R.id.action_profileFragment_to_changeAvatarFragment));
    }
}
