package com.example.movision;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.movision.databinding.ActivityMainBinding;
import javax.inject.Inject;
import architecture.other.ConnectionMonitor;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel viewModel;
    private NavController navController;
    private boolean keepSplashOn = true; // TODO: call this when either login fragment or home fragment is display.

    @Inject
    public ConnectionMonitor connMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        splashScreen.setKeepOnScreenCondition(() -> keepSplashOn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        loadInitially();
        observeStates();
    }

    private void init() {
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        assert host != null;
        navController = host.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavbar, navController);
        getLifecycle().addObserver(connMonitor);
    }

    private void loadInitially() {
        viewModel.loadInitially();
    }

    private void observeStates() {
        viewModel.geSplashState().observe(this, completed -> keepSplashOn = false);
        viewModel.getLoginNavigationState().observe(this, navigate -> navController.navigate(R.id.login));
        viewModel.getGenreNavigationState().observe(this, navigate -> navController.navigate(R.id.peek_genres));
    }
}