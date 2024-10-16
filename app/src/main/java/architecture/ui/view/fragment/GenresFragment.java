package architecture.ui.view.fragment;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.movision.R;
import com.google.android.material.snackbar.Snackbar;
import java.util.Objects;
import architecture.data.model.genre.Genre;
import architecture.ui.view.adapter.GenreAdapter;
import architecture.ui.view.other.ChoseGenresCallback;
import architecture.ui.viewmodel.GenresViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class GenresFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GenresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenresFragment newInstance(String param1, String param2) {
        GenresFragment fragment = new GenresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private GenresViewModel viewModel;
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private Button addButton;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        recyclerView = view.findViewById(R.id.genresRecyclerView);
        addButton = view.findViewById(R.id.addButton);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadInitially();
        bindData();
        setUpBehaviors();
    }

    private void init() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(GenresViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        sharedViewModel.setBottomNavBarVisibility(false);
    }

    private void bindData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new GenreAdapter(getContext(), new ChoseGenresCallback() {
            @Override
            public void chose(Genre genre) {
                viewModel.addGenreToTempList(genre);
            }

            @Override
            public void unChose(Genre genre) {
                viewModel.removeGenreFromTempList(genre);
            }
        }));
        viewModel.getAppGenres().observe(getViewLifecycleOwner(), genres -> {
            ((GenreAdapter) Objects.requireNonNull(recyclerView.getAdapter())).submitList(genres);
        });
    }

    private void setUpBehaviors() {
        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Snackbar.make(addButton, error, Snackbar.LENGTH_SHORT).show());

        viewModel.isFinished().observe(getViewLifecycleOwner(), finished -> {
            if(finished) {
                sharedViewModel.setLoadingHomeDataState(true);
                navController.popBackStack(R.id.homeFragment, false, false);
            }
        });

        addButton.setOnClickListener(view -> {
            viewModel.saveUserGenres();
        });
    }
}