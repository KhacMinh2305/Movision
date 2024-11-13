package architecture.ui.view.fragment;
import static autodispose2.AutoDispose.autoDisposable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.R;
import com.example.movision.databinding.FragmentDiscoverResultBinding;
import java.util.Objects;
import architecture.ui.view.adapter.DiscoverResultAdapter;
import architecture.ui.view.other.DiscoverMovieItemComparator;
import architecture.ui.viewmodel.DiscoverResultViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DiscoverResultFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiscoverResultFragment() {

    }

    public static DiscoverResultFragment newInstance(String param1, String param2) {
        DiscoverResultFragment fragment = new DiscoverResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentDiscoverResultBinding binding;
    private SharedViewModel sharedViewModel;
    private DiscoverResultViewModel viewModel;
    private NavController navController;
    private Bundle args;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            args = getArguments();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiscoverResultBinding.inflate(inflater, container, false);
        initViews();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(this).get(DiscoverResultViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        extractBundle();
        observeData();
        setUpListeners();
    }

    /** @noinspection DataFlowIssue*/
    private void extractBundle() {
        if(args.containsKey("genre")) {
            String genreId = args.getString("genre");
            viewModel.init(null, null, null, null, genreId, null);
            return;
        }
        Float minRate = (args.containsKey("minRate")) ? (float) args.get("minRate") : null;
        float maxRate = 10f;
        Integer minVoteCount = (args.containsKey("minVoteCount")) ? (int) args.get("minVoteCount") : null;
        Integer maxVoteCount = (args.containsKey("maxVoteCount")) ? (int) args.get("maxVoteCount") : null;
        String genresId = args.containsKey("genreId") ? (String) args.get("genresId") : null;
        Integer year = (args.containsKey("year")) ? (int) args.get("year") : null;
        viewModel.init(minRate, maxRate, minVoteCount, maxVoteCount, genresId, year);
    }

    private void initViews() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(new DiscoverResultAdapter(new DiscoverMovieItemComparator(), movieId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("movieId", movieId);
            navController.navigate(R.id.action_discoverResultFragment_to_movieDetailFragment, bundle);
        }));
    }

    private void observeData() {
        viewModel.getData()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(data -> {
            DiscoverResultAdapter adapter = (DiscoverResultAdapter) binding.recyclerView.getAdapter();
            Objects.requireNonNull(adapter).submitData(getLifecycle(), data);
        });
        sharedViewModel.getQuickSearchGenre().observe(getViewLifecycleOwner(), genreId -> {

        });
    }

    private void setUpListeners() {
        binding.backImageButton.setOnClickListener(view ->
                navController.navigateUp());
    }
}