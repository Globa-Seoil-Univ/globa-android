package team.y2k2.globa.main.folder.edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.databinding.ActivityFolderNameEditBinding;

public class FolderNameEditActivity extends AppCompatActivity {
    private FolderNameEditViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFolderNameEditBinding binding = ActivityFolderNameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FolderNameEditViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        initializeData();
        observeViewModel();
    }

    private void initializeData() {
        Intent intent = getIntent();

        int folderId = intent.getIntExtra("folderId", 0);
        String folderTitle = intent.getStringExtra("folderTitle");

        viewModel.setFolderId(folderId);
        viewModel.setFolderName(folderTitle);
    }

    private void observeViewModel() {
        viewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getFinishActivity().observe(this, finish -> {
            if (finish != null && finish) {
                finish();
            }
        });
    }
}