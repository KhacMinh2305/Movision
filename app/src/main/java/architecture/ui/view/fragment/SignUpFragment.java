package architecture.ui.view.fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.example.movision.databinding.FragmentSignUpBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import architecture.ui.viewmodel.SignUpViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;
    private NavController navController;
    private BottomSheetBehavior<ConstraintLayout> signUpSheetBehavior;

    private String userAccount = "";
    private String password = "";
    private String confirmPassword = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(savedInstanceState != null) {
            userAccount = savedInstanceState.getString("gmail");
            password = savedInstanceState.getString("password");
            confirmPassword = savedInstanceState.getString("confirmPassword");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init();
        binding.setViewmodel(viewModel);
        binding.bottomSheet.setState(viewModel.getSheetUiState());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        setUpObservations();
        setUpBehavior();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("gmail", binding.emailSignUpEditText.getText().toString());
        outState.putString("password", binding.passwordSignUpEditText.getText().toString());
        outState.putString("confirmPassword", binding.confirmPasswordEditText.getText().toString());
    }

    private void init() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        signUpSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.emailVerificationSheet);
        restoreState();
        setUpVerificationEditTextFocus();
    }

    private void restoreState() {
        binding.emailSignUpEditText.setText(userAccount);
        binding.passwordSignUpEditText.setText(password);
        binding.confirmPasswordEditText.setText(confirmPassword);
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

    @SuppressLint("SetTextI18n")
    private void setUpObservations() {
        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        });
        viewModel.getSheetUiState().getSheetState().observe(getViewLifecycleOwner(), open -> {
            signUpSheetBehavior.setState(open ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
            binding.signUpButton.setVisibility(open ? View.GONE : View.VISIBLE);
        });
        viewModel.getSheetUiState().getCodeCheckingResult().observe(getViewLifecycleOwner(), match -> {
            if(!match) {
                viewModel.getSheetUiState().clearCode();
            }
        });
        viewModel.getNavigatingState().observe(getViewLifecycleOwner(), navigate -> {
            if(navigate) {
                navController.navigate(R.id.action_signUpFragment_to_genresFragment2);
            }
        });
    }

    private void setUpBehavior() {
        binding.backImageButton.setOnClickListener(view -> {
            navController.navigateUp();
        });
        binding.bottomSheet.backImageButton.setOnClickListener(view -> {
            viewModel.getSheetUiState().setSheetState(false);
        });
        binding.signUpButton.setOnClickListener(view ->
                viewModel.requestVerificationCode(binding.emailSignUpEditText.getText().toString(),
                binding.passwordSignUpEditText.getText().toString(),
                binding.confirmPasswordEditText.getText().toString()));

        binding.bottomSheet.sendCodeTextView.setOnClickListener(view -> viewModel.resendCode());

        binding.bottomSheet.verifySignUpButton.setOnClickListener(view -> {
            String code = binding.bottomSheet.firstCodeEditText.getText().toString()
                    + binding.bottomSheet.secondCodeEditText.getText().toString()
                    + binding.bottomSheet.thirdCodeEditText.getText().toString()
                    + binding.bottomSheet.fourthCodeEditText.getText().toString()
                    + binding.bottomSheet.fifthCodeEditText.getText().toString()
                    + binding.bottomSheet.sixthCodeEditText.getText().toString();
            viewModel.checkCode(code);
        });
    }
}