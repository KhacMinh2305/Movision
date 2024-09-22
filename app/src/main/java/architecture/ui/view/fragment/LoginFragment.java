package architecture.ui.view.fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.R;
import com.example.movision.databinding.FragmentLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import architecture.ui.viewmodel.LoginViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentLoginBinding binding;
    private SharedViewModel sharedViewModel;
    private LoginViewModel viewModel;
    private NavController navController;

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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        init();
        observeStates();
        setUpBehaviors();
        return binding.getRoot();
    }

    private void init() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
    }

    private void observeStates() {
        sharedViewModel.setBottomNavBarVisibility(false);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Snackbar.make(binding.noteTextView, error, Snackbar.LENGTH_LONG).show();
        });
        viewModel.getUserAuthenticated().observe(getViewLifecycleOwner(), authenticated -> {
            if(authenticated) {
                viewModel.login();
            }
        });
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if(success) {
                viewModel.checkLoginFirstTime();
            }
        });
        viewModel.isFirstTimeLogin().observe(getViewLifecycleOwner(), first -> {
            if(first) {
                navController.navigate(R.id.peek_genres);
                return;
            }
            sharedViewModel.setLoadingHomeDataState(true);
            navController.navigateUp();
        });
    }

    private void setUpBehaviors() {
        binding.loginButton.setOnClickListener(view -> {
            String username = binding.usernameEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            if(username.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.noteTextView, "Username or password is empty!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if(viewModel.authLinkAvailable()) {
                viewModel.notifyToHideOrShowAuthView(true);
                return;
            }
            viewModel.checkUserActiveSession(username, password);
        });
        binding.allowButton.setOnClickListener(view -> {
            viewModel.notifyUserClickButton();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.getAuthLink()));
            startActivity(intent);
        });
        binding.denyButton.setOnClickListener(view -> {
            viewModel.notifyToHideOrShowAuthView(false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.checkUserGoForGrantingPermission();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.notifyScreenStop();
    }
}