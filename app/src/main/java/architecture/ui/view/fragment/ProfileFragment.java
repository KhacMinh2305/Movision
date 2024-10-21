package architecture.ui.view.fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.movision.R;
import com.example.movision.databinding.FragmentProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

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
    private BottomSheetBehavior<LinearLayout> sheetBehavior;

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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
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
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        viewModel.init();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.changePasswordSheet);
    }

    private void observeStates() {
        sharedViewModel.setBottomNavBarVisibility(false);
        viewModel.getSignOutNavigationState().observe(getViewLifecycleOwner(),
                navigate -> navController.navigate(R.id.logout));
        sharedViewModel.getImageDataState().observe(getViewLifecycleOwner(),
                bitmap -> Glide.with(this).load(bitmap).into(binding.userShapeableImageView));
        viewModel.getSheetState().observe(getViewLifecycleOwner(), state ->
                sheetBehavior.setState((state) ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED));
        viewModel.getMessageState().observe(getViewLifecycleOwner(), message ->
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
    }

    private void setupEvents() {
        binding.backImageButton.setOnClickListener(v -> {
            sharedViewModel.setBottomNavBarVisibility(true);
            navController.navigateUp();
        });
        binding.signOutButton.setOnClickListener(v -> viewModel.signOut());
        binding.peekAvatarImageButton.setOnClickListener(view ->
                navController.navigate(R.id.action_profileFragment_to_changeAvatarFragment));
        binding.changePasswordTextView.setOnClickListener(view -> viewModel.setSheetState(true));
        binding.bottomSheet.confirmButton.setOnClickListener(view -> {
            String oldPass = binding.bottomSheet.oldPasswordEditText.getText().toString();
            String newPass = binding.bottomSheet.newPasswordEditText.getText().toString();
            viewModel.changePassword(oldPass, newPass);
        });
    }
}
