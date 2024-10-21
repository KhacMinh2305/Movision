package architecture.ui.view.fragment;
import static autodispose2.AutoDispose.autoDisposable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.R;
import com.example.movision.databinding.FragmentPeopleListBinding;
import java.util.Objects;

import architecture.other.AppConstant;
import architecture.ui.view.adapter.PeopleListAdapter;
import architecture.ui.view.other.PeopleComparator;
import architecture.ui.view.other.RecyclerViewItemDecoration;
import architecture.ui.viewmodel.PeopleListViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeopleListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class PeopleListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PeopleListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeopleListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleListFragment newInstance(String param1, String param2) {
        PeopleListFragment fragment = new PeopleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentPeopleListBinding binding;
    private NavController navController;
    private PeopleListViewModel viewModel;
    private String title;
    private String tag;
    private int currFilter = AppConstant.HUMAN_ALL;

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
        binding = FragmentPeopleListBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        observeState();
        initBehaviors();
    }

    private void init() {
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(PeopleListViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        viewModel.init(tag);
    }

    private void initViews() {
        binding.titleTextView.setText(title);
        binding.peopleRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.peopleRecyclerView.setAdapter(new PeopleListAdapter(new PeopleComparator(), getContext(), id -> {
            Log.d("Debug", "Test : " + id);
        }));
        binding.peopleRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(45));
    }

    private void observeState() {
        viewModel.getPeopleLiveData().observe(getViewLifecycleOwner(), pagingDataFlowable -> {
            pagingDataFlowable.to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(peoplePagingData -> {
                        ((PeopleListAdapter) Objects.requireNonNull(binding.peopleRecyclerView.getAdapter()))
                                .submitData(getLifecycle(), peoplePagingData);
                    });
        });
    }

    private void clearData() {
        ((PeopleListAdapter) Objects.requireNonNull(binding.peopleRecyclerView.getAdapter()))
                .submitData(getLifecycle(), PagingData.empty());
    }

    private void applyFilter(int appliedFilter) {
        clearData();
        viewModel.applyFilter(appliedFilter);
    }

    private void updateUiOnFilterChanged(int filter, int uiChange) {
        if(filter == AppConstant.HUMAN_MALE) {
            binding.maleContainer.setBackgroundResource(uiChange);
            return;
        }
        binding.femaleContainer.setBackgroundResource(uiChange);
    }

    private void clearOldFilterIfExist(int oldFilter) {
        if(oldFilter == AppConstant.HUMAN_ALL) {
            return;
        }
        if(oldFilter == AppConstant.HUMAN_MALE) {
            binding.maleCheckBox.setChecked(false);
            return;
        }
        binding.femaleCheckBox.setChecked(false);
    }

    private void reactOnFilterChange(boolean changed, int newFilter) {
        if(changed) {
            int oldFilter = currFilter;
            currFilter = newFilter;
            clearOldFilterIfExist(oldFilter);
            updateUiOnFilterChanged(oldFilter, R.drawable.genre_item_background);
            applyFilter(newFilter);
            updateUiOnFilterChanged(currFilter, R.drawable.common_container_background);
            return;
        }
        if(currFilter == newFilter) {
            applyFilter(AppConstant.HUMAN_ALL);
            updateUiOnFilterChanged(currFilter, R.drawable.genre_item_background);
            currFilter = AppConstant.HUMAN_ALL;
        }
    }

    private void initBehaviors() {
        binding.backImageButton.setOnClickListener(view -> {
            navController.navigateUp();
        });

        binding.maleCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            reactOnFilterChange(checked, AppConstant.HUMAN_MALE);
        });

        binding.femaleCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            reactOnFilterChange(checked, AppConstant.HUMAN_FEMALE);
        });
    }
}