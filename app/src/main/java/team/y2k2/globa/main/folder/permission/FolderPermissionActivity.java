package team.y2k2.globa.main.folder.permission;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityFolderPermissionBinding;

public class FolderPermissionActivity extends AppCompatActivity {
    ActivityFolderPermissionBinding binding;

    FolderPermissionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderPermissionBinding.inflate(getLayoutInflater());

        model = new FolderPermissionModel();

        FolderPermissionItemAdapter adapter = new FolderPermissionItemAdapter(model.getItems());

        binding.recyclerviewFolderPermission.setAdapter(adapter);
        binding.recyclerviewFolderPermission.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        setContentView(binding.getRoot());
    }
}
