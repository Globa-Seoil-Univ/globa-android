package team.y2k2.globa.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_RECORD = 101;
    private static final int REQUEST_CODE_UPLOAD_RECORD = 102;
    private ActivityMainBinding binding;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.setActivity(this);
        // 꼽사리....
        viewModel.getUserIdUpdateToken();
        setContentView(binding.getRoot());
        setNavigationView(binding.navigationMainBottom);
    }

    private void setNavigationView(BottomNavigationView view) {
        view.setOnItemSelectedListener(item -> {
            viewModel.viewFragment(item.getItemId());
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_RECORD && resultCode == RESULT_OK) {
            viewModel.uploadRecord(data);
        }
        if (requestCode == REQUEST_CODE_UPLOAD_RECORD && resultCode == RESULT_OK) {
            viewModel.mainFragment.showRecords(0);
        }
    }

}

