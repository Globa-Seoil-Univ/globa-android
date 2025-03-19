package team.y2k2.globa.main.folder.share;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderShareBinding;
import team.y2k2.globa.main.ProfileImage;

public class FolderShareActivity extends AppCompatActivity {

    private final List<FolderShareItem> itemList = new ArrayList<>();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    ActivityFolderShareBinding binding;
    boolean isSearched = false;
    int folderId;
    private FolderShareActivityModel folderShareActivityModel;
    private FolderShareAdapter adapter;
    private String profile;
    private StorageReference profileImageRef;
    private String lastImageUrl;
    private int tempUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeUI();

    }

    private void initializeUI() {
        folderId = getIntent().getIntExtra("folderId", 0);

        // 코드 입력하는 EditText가 비어있을 땐 X버튼 안보이게 하기
        String input = binding.edittextFolderShareInputName.getText().toString();
        if (input.isEmpty()) {
            binding.buttonFolderShareCancel.setWidth(0);
        }

        // 뒤로가기 버튼
        binding.buttonFolderShareBack.setOnClickListener(v -> finish());

        // 뷰모델 가져오기
        folderShareActivityModel = new ViewModelProvider(this).get(FolderShareActivityModel.class);

        adapter = new FolderShareAdapter(itemList);
        binding.recyclerviewFolderShareSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewFolderShareSelected.setAdapter(adapter);

        initializeEditText();

        binding.constraintlayoutFolderShareSearch.setOnClickListener(v -> {

            if (!binding.textviewFolderShareSearch.getText().toString().isEmpty()) {
                showBottomSheetDialog();
                binding.textviewFolderShareConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
            } else {
                Toast.makeText(FolderShareActivity.this, "사용자를 검색해주세요", Toast.LENGTH_SHORT).show();
            }

        });

        initializeConfirmBtn();
    }

    private void initializeEditText() {
        binding.edittextFolderShareInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    folderShareActivityModel.searchUserInfo(s.toString());

                    folderShareActivityModel.getUserSearchLiveData().observe(FolderShareActivity.this, userInfoResponse -> {
                        if (userInfoResponse != null) {
                            binding.textviewFolderShareSearch.setText(userInfoResponse.getName());
                            profile = userInfoResponse.getProfile();
                            if (profile != null) {
                                if (profile.startsWith("http")) {
                                    Glide.with(FolderShareActivity.this).load(profile).error(R.drawable.profile_user).into(binding.imageviewFolderShareSearch);
                                    lastImageUrl = profile;
                                } else {
                                    profileImageRef = storage.getReference().child(profile);
                                    String firebaseImageUrl = profileImageRef.toString();
                                    lastImageUrl = ProfileImage.convertGsToHttps(firebaseImageUrl);
                                    Glide.with(FolderShareActivity.this).load(lastImageUrl).error(R.mipmap.ic_launcher).into(binding.imageviewFolderShareSearch);
                                }
                            } else {
                                Glide.with(FolderShareActivity.this).load(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewFolderShareSearch);
                                lastImageUrl = "";
                            }

                            tempUserId = userInfoResponse.getUserId();
                            isSearched = true;
                        }
                    });
                    folderShareActivityModel.getErrorLiveData().observe(FolderShareActivity.this, errorMessage -> Log.e("사용자 조회 오류", "사용자 조회 오류: " + errorMessage));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeConfirmBtn() {
        binding.textviewFolderShareConfirm.setOnClickListener(v -> {

            if (adapter.getItemCount() == 0) {
                Toast.makeText(this, "사용자를 추가해주십시오", Toast.LENGTH_SHORT).show();
                return;
            }

            // 리사이클러뷰를 순회하면서 하나씩 API Request
            LinearLayoutManager collectNewUserManager = (LinearLayoutManager) binding.recyclerviewFolderShareSelected.getLayoutManager();
            if (collectNewUserManager != null) {
                int firstPosition = collectNewUserManager.findFirstVisibleItemPosition();
                int lastPosition = collectNewUserManager.findLastVisibleItemPosition();

                for (int i = firstPosition; i <= lastPosition; i++) {
                    RecyclerView.ViewHolder viewHolder = binding.recyclerviewFolderShareSelected.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof FolderShareAdapter.MyViewHolder) {
                        FolderShareAdapter.MyViewHolder adapterViewHolder = (FolderShareAdapter.MyViewHolder) viewHolder;
                        FolderShareItem item = adapter.getItem(i);

                        int userId = item.getUserId();
                        String role = item.getRole();

                        folderShareActivityModel.addSharedUser(folderId, userId, role);
                        Log.d(getClass().getName(), folderId + ", " + userId + ", " + role);
                    }
                }
            }
            folderShareActivityModel.getIsSucceedLiveData().observe(FolderShareActivity.this, responseCode -> {
                if (responseCode.equals("201")) {
                    Toast.makeText(this, "공유 추가 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "공유 추가 권한이 없습니다", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    protected void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_folder_share_authority, null);
        bottomSheetDialog.setContentView(dialogView);

        RelativeLayout readButton = dialogView.findViewById(R.id.relative_layout_folder_share_read);
        RelativeLayout writeButton = dialogView.findViewById(R.id.relative_layout_folder_share_write);

        readButton.setOnClickListener(v -> {
            if (lastImageUrl != null) {
                onClickDialogBtn("r");
            }
            bottomSheetDialog.dismiss();
        });
        writeButton.setOnClickListener(v -> {
            if (lastImageUrl != null) {
                onClickDialogBtn("w");
            }
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void onClickDialogBtn(String role) {
        FolderShareItem newItem = new FolderShareItem(lastImageUrl);
        newItem.setUserId(tempUserId);
        newItem.setRole(role);
        itemList.add(newItem);
        adapter.notifyItemInserted(itemList.size() - 1);
        binding.edittextFolderShareInputName.setText("");
        Glide.with(this).load(R.mipmap.ic_launcher).into(binding.imageviewFolderShareSearch);
        binding.textviewFolderShareSearch.setText("");
        lastImageUrl = null;
    }
}