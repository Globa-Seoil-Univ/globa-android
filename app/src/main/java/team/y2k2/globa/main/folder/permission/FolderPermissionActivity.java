package team.y2k2.globa.main.folder.permission;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.request.FolderPermissionChangeRequest;
import team.y2k2.globa.api.model.response.UserData;
import team.y2k2.globa.api.model.response.UserRole;
import team.y2k2.globa.databinding.ActivityFolderPermissionBinding;

public class FolderPermissionActivity extends AppCompatActivity implements ItemLongClickListener{
    ActivityFolderPermissionBinding binding;
    FolderPermissionViewModel folderPermissionViewModel;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("folderid", MODE_PRIVATE);
        int folderId = preferences.getInt("folderid", 0);

        ArrayList<FolderPermissionItem> itemList = new ArrayList<>();

        folderPermissionViewModel = new ViewModelProvider(this).get(FolderPermissionViewModel.class);
        folderPermissionViewModel.fetchSharedUsers(folderId, 0, 0);
        folderPermissionViewModel.getUsersLiveData().observe(FolderPermissionActivity.this, users -> {
            if(users != null) {
                List<UserRole> userRoles = users.getUsers();
                for(UserRole userRole : userRoles) {
                    UserData user = userRole.getUser();
                    String profile = user.getProfile();
                    String name = user.getName();
                    storageRef = storage.getReference().child(profile);
                    String profilePath = storageRef.getPath();
                    int shareId = userRole.getShareId();
                    int userId = user.getUserId();
                    String role = userRole.getRoleId();

                    if(role.equals("3")) {
                        // 읽기 권한
                        itemList.add(new FolderPermissionItem(name, profilePath, 0, shareId, userId));
                    } else {
                        // 쓰기 권한 or 소유자
                        itemList.add(new FolderPermissionItem(name, profilePath, 1, shareId, userId));
                    }

                }
            }
        });

        FolderPermissionItemAdapter adapter = new FolderPermissionItemAdapter(itemList);

        binding.recyclerviewFolderPermission.setAdapter(adapter);
        binding.recyclerviewFolderPermission.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        binding.textviewFolderPermissionConfirm.setOnClickListener(v -> {

            LinearLayoutManager findDifferenceManager = (LinearLayoutManager) binding.recyclerviewFolderPermission.getLayoutManager();
            if(findDifferenceManager != null) {
                int findDifferencePosition = findDifferenceManager.findFirstCompletelyVisibleItemPosition();
                int lastDifferencePosition = findDifferenceManager.findLastCompletelyVisibleItemPosition();

                for(int i = findDifferencePosition; i <= lastDifferencePosition; i++) {
                    RecyclerView.ViewHolder viewHolder = binding.recyclerviewFolderPermission.findViewHolderForAdapterPosition(i);
                    if(viewHolder instanceof FolderPermissionItemAdapter.AdapterViewHolder) {
                        FolderPermissionItemAdapter.AdapterViewHolder adapterViewHolder = (FolderPermissionItemAdapter.AdapterViewHolder) viewHolder;
                        FolderPermissionItem item = adapter.getItem(i);

                        int shareId = item.getShareId();
                        int userId = item.getUserId();

                        String permission = adapterViewHolder.getPermission();
                        if(permission.equals("쓰기")) {
                            FolderPermissionChangeRequest.setRole("w");
                        } else {
                            FolderPermissionChangeRequest.setRole("r");
                        }

                        folderPermissionViewModel.changeSharedUsers(folderId, shareId, userId);
                    }
                }

            }

        });
    }

    @Override
    public void onItemLongClick(int position) {

    }

}
