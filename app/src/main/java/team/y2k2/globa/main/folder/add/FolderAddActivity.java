package team.y2k2.globa.main.folder.add;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.databinding.ActivityFolderAddBinding;

public class FolderAddActivity extends AppCompatActivity {
    ActivityFolderAddBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonFolderaddBack.setOnClickListener(v -> {
            finish();
        });

        binding.textviewFolderAddConfirm.setOnClickListener(v -> {
            ApiClient apiClient = new ApiClient(this);
            String title = binding.edittextFolderaddInputname.getText().toString();
            apiClient.requestInsertFolder(title);
            finish();
        });
    }
}