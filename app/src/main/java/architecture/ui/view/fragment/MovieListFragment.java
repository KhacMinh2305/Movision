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
import com.example.movision.databinding.FragmentMovieListBinding;
import java.util.Objects;
import architecture.ui.view.adapter.MovieListAdapter;
import architecture.ui.view.other.MovieComparator;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.viewmodel.MovieListViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class MovieListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance(String param1, String param2) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentMovieListBinding binding;
    private MovieListViewModel viewModel;
    private NavController navController;
    private String title;
    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        assert getArguments() != null;
        title = getArguments().getString("title");
        tag = getArguments().getString("tag");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        viewModel.init(tag);
        setupViews();
        bindData();
        setUpBehaviors();
        return binding.getRoot();
    }

    private void setupViews() {
        binding.movieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.movieRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
        binding.movieRecyclerView.setAdapter(new MovieListAdapter(getContext(), (id, movieId) -> {
            Log.d("NAVIGATION", "Navigate to Details fragment with id: " + id + " and movieId: " + movieId);
        }, new MovieComparator()));
    }

    private void bindData() {
        binding.titleTextView.setText(title);
        viewModel.getMovieFlowable()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(moviePagingData -> {
                    ((MovieListAdapter) Objects.requireNonNull(binding.movieRecyclerView.getAdapter()))
                            .submitData(getLifecycle(), moviePagingData);
                });
    }

    private void setUpBehaviors() {
        binding.backImageButton.setOnClickListener(view -> {
            navController.navigateUp();
        });
    }
}