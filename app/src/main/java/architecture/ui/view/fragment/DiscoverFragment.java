package architecture.ui.view.fragment;
import static autodispose2.AutoDispose.autoDisposable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.movision.R;
import com.example.movision.databinding.FragmentDiscoverBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import architecture.other.AppConstant;
import architecture.ui.view.adapter.HistorySearchAdapter;
import architecture.ui.view.adapter.SearchingMovieAdapter;
import architecture.ui.view.adapter.SearchingPeopleAdapter;
import architecture.ui.view.other.SearchingIItemMovieComparator;
import architecture.ui.view.other.SearchingItemPeopleComparator;
import architecture.ui.viewmodel.DiscoverViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DiscoverFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {

    }

    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentDiscoverBinding binding;
    private SharedViewModel sharedViewModel;
    private DiscoverViewModel viewModel;
    private NavController navController;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    private String searchingTag = AppConstant.SEARCH_MOVIE_TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.discoverSheet);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        viewModel.init();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        observeStates();
        setupEventListeners();
    }

    private void initViews() {
        initHistoryRecyclerView();
        initSearchMovieRecyclerView();
        initSearchPeopleRecyclerView();
    }

    private void initHistoryRecyclerView() {
        binding.searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        binding.searchHistoryRecyclerView.setAdapter(new HistorySearchAdapter(new HistorySearchAdapter.SearchQueryItemListener() {
            @Override
            public void onDelete(long id) {
                viewModel.deleteSearchQuery(id);
            }

            @Override
            public void onClick(String query) {
                binding.searchBar.performClick();
                binding.searchView.getEditText().setText(query);
            }
        }));
    }

    private void initSearchMovieRecyclerView() {
        binding.movieRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        binding.movieRecyclerView.setAdapter(new SearchingMovieAdapter(new SearchingIItemMovieComparator(), movieId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("movieId", movieId);
            navController.navigate(R.id.action_discoverFragment_to_movieDetailFragment, bundle);
        }));
    }

    private void initSearchPeopleRecyclerView() {
        binding.peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        binding.peopleRecyclerView.setAdapter(new SearchingPeopleAdapter(new SearchingItemPeopleComparator(), peopleId -> {
            Bundle bundle = new Bundle();
            bundle.putInt("personId", peopleId);
            navController.navigate(R.id.action_discoverFragment_to_peopleFragment, bundle);
        }));
    }

    private void observeStates() {
        observeHistoryQueryState();
        observeGenresSpinnerState();
        observeSearchMovieState();
        observeSearchPeopleState();
    }

    private void observeHistoryQueryState() {
        viewModel.getHistoryQueryState().observe(getViewLifecycleOwner(), searchQueries -> {
            ((HistorySearchAdapter) Objects.requireNonNull(binding.searchHistoryRecyclerView.getAdapter()))
                    .submit(searchQueries);
        });
    }

    private void observeGenresSpinnerState() {
        viewModel.getSpinnerInputState().observe(getViewLifecycleOwner(), genres -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genres);
            binding.bottomSheet.genreSpinner.setAdapter(adapter);
        });
    }

    private void observeSearchMovieState() {
        viewModel.getMovieStreamState().observe(getViewLifecycleOwner(), pagingDataFlowable ->
                pagingDataFlowable.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                        .subscribe(movieItemPagingData ->
                                ((SearchingMovieAdapter) Objects.requireNonNull(binding.movieRecyclerView.getAdapter()))
                                        .submitData(getLifecycle(), movieItemPagingData)));
    }

    private void observeSearchPeopleState() {
        viewModel.getPeopleStreamState().observe(getViewLifecycleOwner(), pagingDataFlowable ->
                pagingDataFlowable.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                        .subscribe(peopleItemPagingData ->
                                ((SearchingPeopleAdapter) Objects.requireNonNull(binding.peopleRecyclerView.getAdapter()))
                                        .submitData(getLifecycle(), peopleItemPagingData)));
    }

    private void setupEventListeners() {
        setDiscoverSheetCallback();
        addQueryChangedListener();
        saveQueryOnChanged();
        setOnQueryFilterChanged();
        setOnHistoryCleared();
        setOnChooseRatingListener();
        discoverMovie();
    }

    private void setOnHistoryCleared() {
        binding.clearHistoryTextView.setOnClickListener(view ->
                viewModel.clearSearchHistory());
    }

    private void setDiscoverSheetCallback() {
        binding.discoverImageButton.setOnClickListener(view ->
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    sharedViewModel.setBottomNavBarVisibility(true);
                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    sharedViewModel.setBottomNavBarVisibility(false);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }

    private void saveQueryOnChanged() {
        binding.searchView.getEditText().setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(keyEvent == null) return false;
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                viewModel.addSearchQuery(textView.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void setOnQueryFilterChanged() {
        binding.movieChip.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                searchingTag = AppConstant.SEARCH_MOVIE_TAG;
                viewModel.onQueryChanged(binding.searchView.getEditText().getText().toString(), searchingTag);
            }
        });

        binding.peopleChip.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                searchingTag = AppConstant.SEARCH_PEOPLE_TAG;
                viewModel.onQueryChanged(binding.searchView.getEditText().getText().toString(), searchingTag);
            }
        });
    }

    private void addQueryChangedListener() {
        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.onQueryChanged(charSequence.toString(), searchingTag);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void setOnChooseRatingListener() {
        binding.bottomSheet.ratingSlider.addOnChangeListener((slider, value, fromUser) ->
                binding.bottomSheet.voteAverageEditText.setText(String.valueOf((int) value)));

        binding.bottomSheet.voteAverageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                if(value.isEmpty()) {
                    binding.bottomSheet.ratingSlider.setValue(0f);
                    return;
                }
                try {
                    int max = Integer.parseInt(value);
                    max = Math.min(max, 10);
                    binding.bottomSheet.ratingSlider.setValue(1f * max);
                } catch(NumberFormatException e) {
                    binding.bottomSheet.ratingSlider.setValue(0f);
                }
            }
        });
    }

    private Bundle putOtherArgs() {
        Bundle args = new Bundle();
        String leastVoteAve = binding.bottomSheet.voteAverageEditText.getText().toString();
        Float voteAve = leastVoteAve.isEmpty() ? null : Float.parseFloat(leastVoteAve);
        String minVoteCount = binding.bottomSheet.leftVoteEditText.getText().toString();
        Integer minVote = minVoteCount.isEmpty() ? null : Integer.parseInt(minVoteCount);
        String maxVoteCount = binding.bottomSheet.rightVoteEditText.getText().toString();
        Integer maxVote = maxVoteCount.isEmpty() ? null : Integer.parseInt(maxVoteCount);
        String chosenYear = binding.bottomSheet.yearEditText.getText().toString();
        Integer year = (chosenYear.isEmpty()) ? null : Integer.parseInt(chosenYear);
        if(voteAve != null) args.putFloat("minRate", voteAve);
        args.putFloat("maxRate", 10f);
        if(minVote != null) args.putInt("minVoteCount", minVote);
        if(maxVote != null) args.putInt("maxVoteCount", maxVote);
        if(year != null) args.putInt("year", year);
        return args;
    }

    private void discoverMovie() {
        binding.bottomSheet.discoverButton.setOnClickListener(view -> {
            String genreName = (String) binding.bottomSheet.genreSpinner.getSelectedItem();
            if(!genreName.isEmpty()) {
                viewModel.getGenreByName(genreName).to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                        .subscribe(genre -> {
                            Bundle args = putOtherArgs();
                            if(genre.getId() != null) {
                                args.putString("genreId", String.valueOf(genre.getId()));
                            }
                            new Handler(Looper.getMainLooper()).post(() ->
                                    navController.navigate(R.id.action_discoverFragment_to_discoverResultFragment, args));
                        });
            }


        });
    }
}