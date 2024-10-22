package architecture.ui.view.fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.example.movision.R;
import com.example.movision.databinding.FragmentLoginBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        observeStates();
        setUpBehaviors();
    }

    private void init() {
        viewModel.init();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewmodel(viewModel);
        binding.bottomSheet.setState(viewModel.getSheetUiState());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.emailVerificationSheet);
        setUpVerificationEditTextFocus();
        //binding.loginButton.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.MIRROR));
    }

    private void setUpFocusListener(EditText currentEditText, EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(currentEditText.getText().toString().isEmpty()) {
                    return;
                }
                currentEditText.clearFocus();
                nextEditText.requestFocus();
            }
        });
    }

    private void setUpVerificationEditTextFocus() {
        binding.bottomSheet.firstCodeEditText.setFocusedByDefault(true);
        setUpFocusListener(binding.bottomSheet.firstCodeEditText, binding.bottomSheet.secondCodeEditText);
        setUpFocusListener(binding.bottomSheet.secondCodeEditText, binding.bottomSheet.thirdCodeEditText);
        setUpFocusListener(binding.bottomSheet.thirdCodeEditText, binding.bottomSheet.fourthCodeEditText);
        setUpFocusListener(binding.bottomSheet.fourthCodeEditText, binding.bottomSheet.fifthCodeEditText);
        setUpFocusListener(binding.bottomSheet.fifthCodeEditText, binding.bottomSheet.sixthCodeEditText);
    }

    //--------------------------------------------------------STATES OBSERVING--------------------------------------------------------
    private void observeStates() {
        sharedViewModel.setBottomNavBarVisibility(false);
        viewModel.getMessageState().observe(getViewLifecycleOwner(),
                message -> Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
        viewModel.getSheetUiState().getSheetState().observe(getViewLifecycleOwner(),
                expand -> sheetBehavior.setState(expand ? BottomSheetBehavior.STATE_EXPANDED
                        : BottomSheetBehavior.STATE_COLLAPSED));
        viewModel.getFocusCodeEditTextState().observe(getViewLifecycleOwner(),
                cleared -> binding.bottomSheet.firstCodeEditText.requestFocus());
        viewModel.getGenreNavigationState().observe(getViewLifecycleOwner(), navigate ->
                navController.navigate(R.id.action_loginFragment_to_genresFragment2));
        viewModel.getHomeNavigatingState().observe(getViewLifecycleOwner(), navigate -> {
            sharedViewModel.setLoadingHomeDataState(true);
            navController.navigateUp();
        });
    }

    //--------------------------------------------------------BEHAVIORS--------------------------------------------------------
    private void setUpBehaviors() {
        binding.loginButton.setOnClickListener(
                view -> viewModel.signInWithEmailAndPassword(binding.emailLoginEditText.getText().toString(),
                binding.passwordLoginEditText.getText().toString()));
        binding.googleImageButton.setOnClickListener(view -> viewModel.signInWithGoogle(requireActivity()));
        //binding.facebookImageButton.setOnClickListener(view -> viewModel.signInWithFacebook(this)); //(Do not remove this !!!)
        binding.facebookImageButton.setOnClickListener(view ->
                Snackbar.make(binding.getRoot(),
                        "This feature has is not stable on this version. It will be brought back in the next updates.",
                        Snackbar.LENGTH_SHORT).show());
        binding.signUpImageView.setOnClickListener(view -> navController.navigate(R.id.action_loginFragment_to_signUpFragment));
        binding.forgotPasswordTextView.setOnClickListener(view -> {
            viewModel.sendCodeForUpdatingPassword(binding.emailLoginEditText.getText().toString());
        });
        binding.bottomSheet.backImageButton.setOnClickListener(view -> {
            viewModel.setSendingState(false);
            viewModel.getSheetUiState().setSheetState(false);
        });
        binding.bottomSheet.verifySignUpButton.setOnClickListener(view -> {
            viewModel.verifyCode(getUserInputCode());
        });
        binding.bottomSheet.sendCodeTextView.setOnClickListener(view -> {
            viewModel.sendCodeAgain();
        });
    }

    //--------------------------------------------------------BUSINESSES--------------------------------------------------------
    private String getUserInputCode() {
        return binding.bottomSheet.firstCodeEditText.getText().toString()
                + binding.bottomSheet.secondCodeEditText.getText().toString()
                + binding.bottomSheet.thirdCodeEditText.getText().toString()
                + binding.bottomSheet.fourthCodeEditText.getText().toString()
                + binding.bottomSheet.fifthCodeEditText.getText().toString()
                + binding.bottomSheet.sixthCodeEditText.getText().toString();
    }

    //--------------------------------------------------------OTHERS--------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.getResultFromFacebookTokenRequest(requestCode, resultCode, data);
    }
}
