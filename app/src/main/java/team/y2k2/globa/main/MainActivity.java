package team.y2k2.globa.main;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMainBinding;
import team.y2k2.globa.docs.upload.DocsUploadActivity;
import team.y2k2.globa.login.LoginViewModel;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.profile.ProfileFragment;
import team.y2k2.globa.main.statistics.StatisticsFragment;

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
        setContentView(binding.getRoot());
        setNavigationView(binding.navigationMainBottom);
    }

    private void setNavigationView(BottomNavigationView view) {
        view.setOnItemSelectedListener(item -> {
            viewModel.viewFragment(item.getItemId());
            view.setSelectedItemId(item.getItemId());
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
            viewModel.mainFragment.showCurrentlyAddedRecords();
        }
    }
}

