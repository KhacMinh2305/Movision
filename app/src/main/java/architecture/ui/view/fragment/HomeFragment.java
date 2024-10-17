package architecture.ui.view.fragment;
import static architecture.other.AppConstant.POPULAR_PEOPLE_TITLE;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.movision.R;
import com.example.movision.databinding.FragmentHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import architecture.other.AppConstant;
import architecture.ui.view.adapter.HomeGenreAdapter;
import architecture.ui.view.adapter.MovieAdapter;
import architecture.ui.view.adapter.PeopleAdapter;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.viewmodel.HomeViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentHomeBinding binding;
    private SharedViewModel sharedViewModel;
    private HomeViewModel viewModel;
    private NavController navController;
    private BottomSheetBehavior<FrameLayout> genresSheetBehavior;

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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        loadData();
        bindData();
        setUpBehavior();
        test();
    }

    //--------------------------------------------------------INITIALIZATION--------------------------------------------------------
    private void init() {
        binding.setSharedViewModel(sharedViewModel);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        getLifecycle().addObserver(binding.bannerImageSlider);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        initViews();
    }

    private void initViews() {
        initGenresRecyclerViews();
        initPreviewMovieViews();
        initPeople();
        sharedViewModel.setBottomNavBarVisibility(true);
    }

    private void initGenresRecyclerViews() {
        binding.userGenresRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.userGenresRecyclerView.setAdapter(new HomeGenreAdapter(getContext(), R.layout.user_genre_item, genre -> {
            Log.d("Debug", "Move to Discover fragment !");
        }));
        binding.genresBottomSheet.peekGenresRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.genresBottomSheet.peekGenresRecyclerView.setAdapter(new HomeGenreAdapter(getContext(), R.layout.user_genre_item, genre -> {
            viewModel.removeGenreFromUserList(genre);
        }));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.genresBottomSheet.genresStoreRecyclerView.setLayoutManager(gridLayoutManager);
        binding.genresBottomSheet.genresStoreRecyclerView.setAdapter(new HomeGenreAdapter(getContext(), R.layout.grid_genres_item, genre -> {
            viewModel.addGenreFromUserList(genre);
        }));
    }

    private void initPreviewMovieViews() {
        binding.trendingPreview.seeMoreTextView.setText(R.string.see_more);
        binding.trendingPreview.titleTextView.setText(AppConstant.CATEGORY_TRENDING_TITLE);
        initPreviewRecyclerViews(binding.trendingPreview.recyclerView);

        binding.topRatedPreview.seeMoreTextView.setText(R.string.see_more);
        binding.topRatedPreview.titleTextView.setText(AppConstant.CATEGORY_TOP_RATED_TITLE);
        initPreviewRecyclerViews(binding.topRatedPreview.recyclerView);

        binding.popularPreview.seeMoreTextView.setText(R.string.see_more);
        binding.popularPreview.titleTextView.setText(AppConstant.CATEGORY_POPULAR_TITLE);
        initPreviewRecyclerViews(binding.popularPreview.recyclerView);

        binding.playingPreview.seeMoreTextView.setText(R.string.see_more);
        binding.playingPreview.titleTextView.setText(AppConstant.CATEGORY_PLAYING_TITLE);
        initPreviewRecyclerViews(binding.playingPreview.recyclerView);

        binding.randomGenres.seeMoreTextView.setText(R.string.see_more);
        initPreviewRecyclerViews(binding.randomGenres.recyclerView);
    }

    private void initPreviewRecyclerViews(RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.addItemDecoration(new RecyclerViewItemDecoration(45));
        rv.setAdapter(new MovieAdapter(getContext(), R.layout.preview_movie_item, (id, movieId) -> {
            Log.d("Debug", "Go to details");
        }));
    }

    private void initPeople() {
        binding.popularPeople.seeMoreTextView.setText(R.string.see_more);
        binding.popularPeople.titleTextView.setText(POPULAR_PEOPLE_TITLE);
        binding.popularPeople.recyclerView.
                setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularPeople.recyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
        binding.popularPeople.recyclerView.setAdapter(new PeopleAdapter(getContext(), R.layout.home_people_item, id -> {
            Log.d("Debug", "Vao thang nguoi chi tiet !");
        }));
    }

    //--------------------------------------------------------LOAD DATA--------------------------------------------------------
    private void loadData() {
        sharedViewModel.getLoadingHomeScreenDataState().observe(getViewLifecycleOwner(), signal -> {
            if(signal) {
                viewModel.resetLoading();
                viewModel.loadInit();
                // emit false because loading will be executed when observe LiveData again after navigating back
                sharedViewModel.setLoadingHomeDataState(false);
            }
        });
    }

    //--------------------------------------------------------BIND DATA--------------------------------------------------------
    private void bindData() {
        viewModel.getListUpcomingMoviePosterUrls().observe(getViewLifecycleOwner(), urls -> { // demo
            binding.bannerImageSlider.setListUrls(this, urls);
        });
        bindGenres();
        bindPreviewRecyclerMovie();
        bindPeople();
        bindPersonalMovie();
        observeAvatarChange();
    }

    private void bindPreviewRecyclerMovie() {
        viewModel.getPreviewTrendingMovies().observe(getViewLifecycleOwner(), movies ->
                ((MovieAdapter) Objects.requireNonNull(binding.trendingPreview.recyclerView.getAdapter())).submitList(movies));
        viewModel.getPreviewTopRatedMovies().observe(getViewLifecycleOwner(), movies ->
                ((MovieAdapter) Objects.requireNonNull(binding.topRatedPreview.recyclerView.getAdapter())).submitList(movies));
        viewModel.getPreviewPopularMovies().observe(getViewLifecycleOwner(), movies ->
                ((MovieAdapter) Objects.requireNonNull(binding.popularPreview.recyclerView.getAdapter())).submitList(movies));
        viewModel.getPreviewPlayingMovies().observe(getViewLifecycleOwner(), movies ->
                ((MovieAdapter) Objects.requireNonNull(binding.playingPreview.recyclerView.getAdapter())).submitList(movies));
    }

    private void bindGenres() {
        viewModel.getListUserGenres().observe(getViewLifecycleOwner(), genres ->
                ((HomeGenreAdapter) Objects.requireNonNull(binding.userGenresRecyclerView.getAdapter())).submitList(genres));
        viewModel.getUserGenresTemp().observe(getViewLifecycleOwner(), genres ->
                ((HomeGenreAdapter) Objects.requireNonNull(binding.genresBottomSheet.peekGenresRecyclerView.getAdapter())).submitList(genres));
        viewModel.getListGenresToPeek().observe(getViewLifecycleOwner(), genres ->
                ((HomeGenreAdapter) Objects.requireNonNull(binding.genresBottomSheet.genresStoreRecyclerView.getAdapter())).submitList(genres));
    }

    private void bindPeople() {
        viewModel.getPreviewPopularPeople().observe(getViewLifecycleOwner(), people ->
                ((PeopleAdapter) Objects.requireNonNull(binding.popularPeople.recyclerView.getAdapter())).submitList(people));
    }

    private void bindPersonalMovie() {
        viewModel.getPersonalGenre().observe(getViewLifecycleOwner(), genre ->
                binding.randomGenres.titleTextView.setText(genre.getName()));
        viewModel.getPersonalMovies().observe(getViewLifecycleOwner(), movies ->
                ((MovieAdapter) Objects.requireNonNull(binding.randomGenres.recyclerView.getAdapter())).submitList(movies));
    }

    private void observeAvatarChange() {
        sharedViewModel.getImageDataState().observe(getViewLifecycleOwner(), bitmap ->
                Glide.with(this).load(bitmap).into(binding.userShapeableImageView));
    }

    //--------------------------------------------------------BEHAVIOR--------------------------------------------------------

    private void setUpBehavior() {
        setupGenresBehaviors();
        seeMore();
    }

    private void setupGenresBehaviors() {
        genresSheetBehavior = BottomSheetBehavior.from(binding.genresBottomSheet.genreBottomSheet);
        binding.addGenresButton.setOnClickListener(view -> {
            genresSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            sharedViewModel.setBottomNavBarVisibility(false);
        });
        binding.genresBottomSheet.hideButton.setOnClickListener(view -> {
            genresSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            sharedViewModel.setBottomNavBarVisibility(true);
        });
        binding.genresBottomSheet.confirmAddGenresButton.setOnClickListener(view -> viewModel.updateUserGenres());
    }

    private Bundle createBundle(String title, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("tag", tag);
        return bundle;
    }

    private void seeMore() {
        // movies
        binding.trendingPreview.seeMoreTextView.setOnClickListener(view ->
                navController.navigate(R.id.action_homeFragment_to_movieListFragment,
                createBundle(AppConstant.CATEGORY_TRENDING_TITLE, AppConstant.CATEGORY_TRENDING_TAG)));

        binding.topRatedPreview.seeMoreTextView.setOnClickListener(view ->
                navController.navigate(R.id.action_homeFragment_to_movieListFragment,
                createBundle(AppConstant.CATEGORY_TOP_RATED_TITLE, AppConstant.CATEGORY_TOP_RATED_TAG)));

        binding.popularPreview.seeMoreTextView.setOnClickListener(view ->
                navController.navigate(R.id.action_homeFragment_to_movieListFragment,
                createBundle(AppConstant.CATEGORY_POPULAR_TITLE, AppConstant.CATEGORY_POPULAR_TAG)));

        binding.playingPreview.seeMoreTextView.setOnClickListener(view ->
                navController.navigate(R.id.action_homeFragment_to_movieListFragment,
                createBundle(AppConstant.CATEGORY_PLAYING_TITLE, AppConstant.CATEGORY_PLAYING_TAG)));

        // people
        binding.popularPeople.seeMoreTextView.setOnClickListener(view -> navController.navigate(R.id.action_homeFragment_to_peopleListFragment, createBundle(POPULAR_PEOPLE_TITLE,
                AppConstant.POPULAR_PEOPLE_TAG)));
    }

    //--------------------------------------------------------TEST--------------------------------------------------------
    private void test() {
        binding.userShapeableImageView.setOnClickListener(view ->
                navController.navigate(R.id.action_homeFragment_to_profileFragment));
    }
}