package com.example.movision;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.movision.databinding.ActivityMainBinding;
import architecture.ui.viewmodel.SharedViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel viewModel;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        getLifecycle().addObserver(viewModel.getConnectionMonitor());
        initNavigation();
        login();
    }

    private void initNavigation() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        assert host != null;
        navController = host.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavbar, navController);
    }

    private void login() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.splashScreen.setVisibility(View.GONE);
        }, 1500);
        navController.navigate(R.id.login);
    }
}