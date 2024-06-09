package team.y2k2.globa.main.folder.permission;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

public class FolderPermissionActivity extends AppCompatActivity{
    ActivityFolderPermissionBinding binding;
    FolderPermissionViewModel folderPermissionViewModel;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    int folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        folderId = getIntent().getIntExtra("folderId", 0);

        ArrayList<FolderPermissionItem> itemList = new ArrayList<>();

        folderPermissionViewModel = new ViewModelProvider(this).get(FolderPermissionViewModel.class);
        folderPermissionViewModel.fetchSharedUsers(folderId, 1, 10);


        folderPermissionViewModel.getUsersLiveData().observe(FolderPermissionActivity.this, users -> {
            if(users != null) {
                List<UserRole> userRoles = users.getUsers();
                for(UserRole userRole : userRoles) {
                    UserData user = userRole.getUser();
                    String profile = user.getProfile();
                    String name = user.getName();
                    Log.d(getClass().getName(), "사용자 이미지 경로: " + profile);
                    Log.d(getClass().getName(), "사용자 이름: " + name);
                    String profilePath = "";
                    if(profile != null) {
                        storageRef = storage.getReference().child(profile);
                        profilePath = storageRef.getPath();
                    }
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


                FolderPermissionItemAdapter adapter = new FolderPermissionItemAdapter(itemList);

                binding.recyclerviewFolderPermission.setAdapter(adapter);
                binding.recyclerviewFolderPermission.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                binding.textviewFolderPermissionConfirm.setOnClickListener(v -> {

                    LinearLayoutManager findDifferenceManager = (LinearLayoutManager) binding.recyclerviewFolderPermission.getLayoutManager();
                    if(findDifferenceManager != null) {
                        int findDifferencePosition = findDifferenceManager.findFirstCompletelyVisibleItemPosition();
                        int lastDifferencePosition = findDifferenceManager.findLastCompletelyVisibleItemPosition();

                        for(int i = findDifferencePosition + 1; i <= lastDifferencePosition; i++) {
                            RecyclerView.ViewHolder viewHolder = binding.recyclerviewFolderPermission.findViewHolderForAdapterPosition(i);
                            if(viewHolder instanceof FolderPermissionItemAdapter.AdapterViewHolder) {
                                FolderPermissionItemAdapter.AdapterViewHolder adapterViewHolder = (FolderPermissionItemAdapter.AdapterViewHolder) viewHolder;
                                FolderPermissionItem item = adapter.getItem(i);

                                int shareId = item.getShareId();
                                int userId = item.getUserId();
                                String userRole = "";

                                String permission = adapterViewHolder.getPermission();
                                if(permission.equals("수정")) {
                                    userRole = "w";
                                } else {
                                    userRole = "r";
                                }

                                Log.d("Request 정보" ,"폴더 아이디: " + folderId + ", 공유 아이디: " + shareId + ", 유저 아이디: " + userId + ", 권한: " + userRole);

                                folderPermissionViewModel.changeSharedUsers(folderId, userId, userRole);
                            }
                        }

                    }

                });

                adapter.setOnItemLongClickListener(new FolderPermissionItemAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {

                        FolderPermissionItem item = adapter.getItem(position);

                        int userId = item.getUserId();
                        folderPermissionViewModel.deleteSharedUsers(folderId, userId);

                    }
                });

            }
        });



        binding.imagebuttonFolderPermissionBack.setOnClickListener(v -> {
            finish();
        });
    }


}
