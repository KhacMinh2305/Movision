package architecture.ui.view.fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import com.example.movision.R;
import com.example.movision.databinding.FragmentMovieDetailBinding;
import com.google.android.material.snackbar.Snackbar;
import architecture.data.model.movie.in_app.ClipUrl;
import architecture.ui.view.adapter.MovieCasterAdapter;
import architecture.ui.view.adapter.MovieClipAdapter;
import architecture.ui.view.adapter.SimilarMovieAdapter;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.viewmodel.MovieDetailViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class MovieDetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(String param1, String param2) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentMovieDetailBinding binding;
    private MovieDetailViewModel viewModel;
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false);
        //sheetBehavior = BottomSheetBehavior.from(binding.reviewSheet.movieReviewBottomSheet);
        WebSettings settings = binding.playerWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        loadInitially();
        observeStates();
        setupEvent();
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        navController = Navigation.findNavController(binding.getRoot());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
    }

    private void loadInitially() {
        viewModel.init(movieId);
    }

    private void observeMessage() {
        viewModel.getMessageState().observe(getViewLifecycleOwner(), message ->
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
    }

    private void observeMovieCastersState() {
        viewModel.getCastersState().observe(getViewLifecycleOwner(), casters -> {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
            binding.casterRecyclerView.setLayoutManager(layoutManager);
            MovieCasterAdapter adapter = new MovieCasterAdapter(getContext(), id -> {
                Bundle bundle = new Bundle();
                bundle.putInt("personId", id);
                navController.navigate(R.id.action_movieDetailFragment_to_peopleFragment, bundle);
            });
            binding.casterRecyclerView.setAdapter(adapter);
            binding.casterRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
            adapter.submit(casters);
        });
    }

    private void observeMovieClipState() {
        viewModel.getMovieClipsState().observe(getViewLifecycleOwner(), clipUrls -> {
            MovieClipAdapter adapter = new MovieClipAdapter(getContext(), clipUrls);
            binding.clipRecyclerView.setAdapter(adapter);
            binding.clipRecyclerView.initOnBindData(index -> {
                ClipUrl clipUrl = adapter.getListData().get(index);
                binding.playerWebView.loadUrl(clipUrl.getUrl());
            });
        });
    }

    private void observeSimilarMoviesState() {
        viewModel.getSimilarMovieState().observe(getViewLifecycleOwner(), similarMovies -> {
            binding.recommendationRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.recommendationRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
            SimilarMovieAdapter adapter =  new SimilarMovieAdapter(getContext(), id -> {
                navController.navigate(R.id.action_movieDetailFragment_self, createBundle(id));
            });
            binding.recommendationRecyclerView.setAdapter(adapter);
            adapter.submitList(similarMovies);
        });
    }

    private void observeStates() {
        observeMessage();
        observeMovieCastersState();
        observeMovieClipState();
        observeSimilarMoviesState();
        viewModel.getFavoriteState().observe(getViewLifecycleOwner(), favorite -> {
            if(favorite) {
                binding.favoriteCheckBox.setChecked(true);
            }
        });
    }

    private void loadNewClip(String url, int currIndex) {
        binding.playerWebView.loadUrl(url);
        binding.clipRecyclerView.smoothScrollToPosition(currIndex);
        binding.clipRecyclerView.syncSnappedIndex(currIndex);
    }

    private Bundle createBundle(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", id);
        return bundle;
    }

    private void setupEvent() {
        binding.backImageButton.setOnClickListener(view -> navController.navigateUp());
        binding.backwardImageButton.setOnClickListener(view -> {
            int currIndex = binding.clipRecyclerView.getSnappedIndex();
            if(currIndex <= 0) return;
            MovieClipAdapter adapter = (MovieClipAdapter) binding.clipRecyclerView.getAdapter();
            if(adapter == null) return;
            currIndex--;
            ClipUrl clipUrl = adapter.getListData().get(currIndex);
            loadNewClip(clipUrl.getUrl(), currIndex);
        });
        binding.towardImageButton.setOnClickListener(view -> {
            int currIndex = binding.clipRecyclerView.getSnappedIndex();
            MovieClipAdapter adapter = (MovieClipAdapter) binding.clipRecyclerView.getAdapter();
            if(adapter == null) return;
            int lastIndex = adapter.getListData().size() - 1;
            if(currIndex >= lastIndex) return;
            currIndex++;
            ClipUrl clipUrl = adapter.getListData().get(currIndex);
            loadNewClip(clipUrl.getUrl(), currIndex);
        });
        binding.reviewImageButton.setOnClickListener(view ->
                navController.navigate(R.id.action_movieDetailFragment_to_reviewFragment, createBundle(movieId)));
        addRating();
        onCheckFavoriteCheckBox();
    }

    private void addRating() {
        binding.rateCheckBox.setOnCheckedChangeListener((compoundButton, checked) ->
                RatingDialogFragment.newInstance(rating -> {
                    if (rating < 0) {
                        return;
                    }
                    viewModel.rateMovie((float) rating);
                })
                .show(getChildFragmentManager(), RatingDialogFragment.TAG));
    }

    private void onCheckFavoriteCheckBox() {
        binding.favoriteCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                viewModel.addToFavoriteList();
                return;
            }
            viewModel.removeFromFavoriteList();
        });
    }
}