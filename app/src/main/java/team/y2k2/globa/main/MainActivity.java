package team.y2k2.globa.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityMainBinding;

import static team.y2k2.globa.main.MainActivityModel.*;

public class MainActivity extends AppCompatActivity {
    private MainActivityModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainActivityModel.class);
        viewModel.setContext(this);
        viewModel.handleUserFcmToken();

        setContentView(binding.getRoot());
        setNavigationView(binding.bottomNavigationMainBottom);
        observeLiveData();
    }

    private void setNavigationView(BottomNavigationView view) {
        view.setOnItemSelectedListener(item -> {
            viewModel.viewFragment(item.getItemId());
            return true;
        });
    }

    private void observeLiveData() {
        viewModel.getSelectedFragment().observe(this, fragment -> {
            if (fragment != null) {
                replaceFragment(fragment);
            }
        });

        viewModel.getShowBottomSheetDialog().observe(this, show -> {
            if (show) {
                showAudioSelectionDialog();
                viewModel.getShowBottomSheetDialog().setValue(false);
            }
        });

        viewModel.getUploadRecordIntent().observe(this, intent -> {
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_UPLOAD_RECORD);
                viewModel.getUploadRecordIntent().setValue(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPLOAD_RECORD && resultCode == RESULT_OK) {
            viewModel.refreshMainFragmentRecords();
        }
        if (requestCode == REQUEST_CODE_PICK_RECORD && resultCode == RESULT_OK) {
            viewModel.uploadRecord(data);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container_view_main, fragment, null).commit();
    }

    private void showAudioSelectionDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getApplication().getApplicationContext());
        bottomSheetDialog.setContentView(R.layout.dialog_upload);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(TYPE_AUDIO);
        startActivityForResult(intent, REQUEST_CODE_PICK_RECORD);
    }
}