package architecture.ui.view.fragment;
import static autodispose2.AutoDispose.autoDisposable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.R;
import com.example.movision.databinding.FragmentReviewBinding;
import java.util.Objects;
import architecture.ui.view.adapter.MovieReviewAdapter;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.view.other.ReviewComparator;
import architecture.ui.viewmodel.ReviewViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentReviewBinding binding;
    private SharedViewModel sharedViewModel;
    private ReviewViewModel viewModel;
    private NavController navController;
    private int movieId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            movieId = getArguments().getInt("movieId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.init(movieId);
        intiViews();
        observeStates();
        setupEventListeners();
    }

    private void init() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    private void intiViews() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        binding.reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.reviewRecyclerView.setAdapter(new MovieReviewAdapter(new ReviewComparator()));
        binding.reviewRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
    }

    private void observeStates() {
        sharedViewModel.setBottomNavBarVisibility(false);
        viewModel.getReviewFlowable()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(movieReviewPagingData -> {
                    MovieReviewAdapter adapter = (MovieReviewAdapter) binding.reviewRecyclerView.getAdapter();
                    assert adapter != null;
                    adapter.submitData(getLifecycle(), movieReviewPagingData);
                }, throwable -> Log.d("ERROR", throwable.toString()));

        viewModel.getAddReviewState().observe(getViewLifecycleOwner(), success ->
                ((MovieReviewAdapter) Objects.requireNonNull(binding.reviewRecyclerView.getAdapter())).refresh());
    }

    private void setupEventListeners() {
        binding.backImageButton.setOnClickListener(view -> {
            sharedViewModel.setBottomNavBarVisibility(true);
            navController.navigateUp();
        });
        binding.sendImageButton.setOnClickListener(view -> {
            String review = binding.reviewEditText.getText().toString();
            if(review.isEmpty()) return;
            binding.reviewEditText.setText("");
            viewModel.addMovieReview(review);
        });
    }
}