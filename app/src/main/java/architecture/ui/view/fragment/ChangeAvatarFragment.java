package architecture.ui.view.fragment;
import static autodispose2.AutoDispose.autoDisposable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movision.R;
import com.example.movision.databinding.FragmentChangeAvatarBinding;
import java.util.Objects;
import javax.inject.Inject;
import architecture.data.repo.MediaRepository;
import architecture.domain.BitmapProcessor;
import architecture.ui.viewmodel.ChangeAvatarViewModel;
import architecture.ui.viewmodel.SharedViewModel;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.Single;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeAvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


@AndroidEntryPoint
public class ChangeAvatarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangeAvatarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeAvatarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeAvatarFragment newInstance(String param1, String param2) {
        ChangeAvatarFragment fragment = new ChangeAvatarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentChangeAvatarBinding binding;
    private SharedViewModel sharedViewModel;
    private ChangeAvatarViewModel viewModel;
    private NavController navController;

    private final ActivityResultLauncher<PickVisualMediaRequest> openGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if(uri != null) {
                    binding.cropImageView.setImageUriAsync(uri);
                }
            });

    private final ActivityResultLauncher<Intent> openCameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Bitmap bitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                    binding.cropImageView.setImageBitmap(bitmap);
                }
            });

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
        binding = FragmentChangeAvatarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        observeStates();
        setupEvents();
    }

    private void init() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(ChangeAvatarViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewmodel(viewModel);
    }

    private void observeStates() {
        viewModel.getNavigateState().observe(getViewLifecycleOwner(), imageData -> {
            sharedViewModel.setImageDataState(imageData);
            navController.navigateUp();
        });
    }

    private void setupEvents() {
        binding.backImageButton.setOnClickListener(view -> navController.navigateUp());

        binding.openGalleryImageButton.setOnClickListener(view ->
                openGalleryLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        binding.openCameraImageButton.setOnClickListener(view ->
                openCameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)));

        binding.confirmImageButton.setOnClickListener(view -> Single.fromCallable(() ->
                binding.cropImageView.getCroppedImage(100, 100))
                .map(bitmap -> (new BitmapProcessor()).compressBitmap(bitmap)
                ).to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(bytes -> viewModel.changeUserAvatar(bytes),
                        throwable -> Log.d("Debug", throwable.toString())));
    }
}

/*
Image picking process has an bug : It does not work match with the navigation component
It will navigate user to home screen (the dedault activity). Because of this , using
BottomSheetLayout instead of an fragmen may solve the problem*/
