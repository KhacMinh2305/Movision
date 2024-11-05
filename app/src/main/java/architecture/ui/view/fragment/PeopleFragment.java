package architecture.ui.view.fragment;
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
import com.example.movision.databinding.FragmentPeopleBinding;
import architecture.ui.view.adapter.ImageAdapter;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.viewmodel.PeopleViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PeopleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PeopleFragment() {

    }

    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentPeopleBinding binding;
    private PeopleViewModel viewModel;
    private NavController navController;
    private int personId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            personId = getArguments().getInt("personId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPeopleBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PeopleViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.init(personId);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.setLifecycleOwner(getViewLifecycleOwner());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        observeStates();
        setupEventListeners();
    }

    private void observeStates() {
        viewModel.getImagesAlbumState().observe(getViewLifecycleOwner(), posterPaths -> {
            Log.d("Test", posterPaths.size() + "");
            binding.albumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()
                    , LinearLayoutManager.HORIZONTAL, false));
            binding.albumRecyclerView.setAdapter(new ImageAdapter(posterPaths));
            binding.albumRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
        });
    }

    private void setupEventListeners() {
        binding.backImageButton.setOnClickListener(view -> navController.navigateUp());
    }
}

/*
        binding.movieShapeableImageView.setRenderEffect(RenderEffect.createBlurEffect(16, 16, Shader.TileMode.MIRROR));
* */