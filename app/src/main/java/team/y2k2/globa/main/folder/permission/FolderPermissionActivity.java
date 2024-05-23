package team.y2k2.globa.main.folder.permission;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.response.UserData;
import team.y2k2.globa.api.model.response.UserRole;
import team.y2k2.globa.databinding.ActivityFolderPermissionBinding;

public class FolderPermissionActivity extends AppCompatActivity {
    ActivityFolderPermissionBinding binding;
    FolderPermissionViewModel folderPermissionViewModel;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderPermissionBinding.inflate(getLayoutInflater());

        ArrayList<FolderPermissionItem> itemList = new ArrayList<>();

        folderPermissionViewModel = new ViewModelProvider(this).get(FolderPermissionViewModel.class);
        folderPermissionViewModel.fetchSharedUsers("", 0, 0);
        folderPermissionViewModel.getUsersLiveData().observe(FolderPermissionActivity.this, users -> {
            if(users != null) {
                List<UserRole> userRoles = users.getUsers();
                for(UserRole userRole : userRoles) {
                    UserData user = userRole.getUser();
                    String profile = user.getProfile();
                    String name = user.getName();
                    storageRef = storage.getReference().child(profile);
                    String profilePath = storageRef.getPath();

                    itemList.add(new FolderPermissionItem(name, profilePath, 0));

                }
            }
        });

        FolderPermissionItemAdapter adapter = new FolderPermissionItemAdapter(itemList);

        binding.recyclerviewFolderPermission.setAdapter(adapter);
        binding.recyclerviewFolderPermission.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        setContentView(binding.getRoot());
    }
}
