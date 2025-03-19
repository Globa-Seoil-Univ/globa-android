package team.y2k2.globa.main.folder.add;


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
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.ShareTarget;
import team.y2k2.globa.databinding.ActivityFolderAddBinding;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.folder.share.FolderShareActivityModel;

public class FolderAddActivity extends AppCompatActivity {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final List<FolderAddItem> itemList = new ArrayList<>();
    private final List<ShareTarget> shareTargetList = new ArrayList<>();
    ActivityFolderAddBinding binding;
    ApiClient apiClient;
    FolderShareActivityModel folderShareActivityModel;
    String profile;
    String newProfile;
    String code;
    FolderAddAdapter adapter;
    StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        folderShareActivityModel = new ViewModelProvider(this).get(FolderShareActivityModel.class);

        apiClient = new ApiClient(this);

        adapter = new FolderAddAdapter(itemList, this);
        binding.recyclerviewFolderAddShareSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewFolderAddShareSelected.setAdapter(adapter);

        binding.buttonFolderAddBack.setOnClickListener(v -> finish());

        binding.textviewFolderAddConfirm.setOnClickListener(v -> {

            if (binding.edittextFolderAddInputName.getText().length() == 0) {
                Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (adapter.getItemCount() != 0) {
                LinearLayoutManager collectNewUserManager = (LinearLayoutManager) binding.recyclerviewFolderAddShareSelected.getLayoutManager();
                if (collectNewUserManager != null) {
                    int firstPosition = collectNewUserManager.findFirstVisibleItemPosition();
                    int lastPosition = collectNewUserManager.findLastVisibleItemPosition();

                    for (int i = firstPosition; i <= lastPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = binding.recyclerviewFolderAddShareSelected.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof FolderAddAdapter.MyViewHolder) {
                            FolderAddAdapter.MyViewHolder adapterViewHolder = (FolderAddAdapter.MyViewHolder) viewHolder;
                            FolderAddItem item = adapter.getItem(i);

                            String tempCode = item.getCode();
                            String tempRole = item.getRole();

                            shareTargetList.add(new ShareTarget(tempCode, tempRole));

                        }
                    }
                }
            }

            String title = binding.edittextFolderAddInputName.getText().toString();

            if (!shareTargetList.isEmpty()) {
                Log.d(getClass().getSimpleName(), "제목: " + title + ", 공유대상: " + shareTargetList.get(0).getCode() + ", " + shareTargetList.get(0).getRole());
            }

            apiClient.requestInsertFolder(title, shareTargetList);

            finish();

        });

        binding.edittextFolderAddInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewFolderAddCount.setText(s.length() + "/32");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    binding.textviewFolderAddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                } else {
                    binding.textviewFolderAddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 32) {
                    binding.edittextFolderAddInputName.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextFolderAddInputName.setText(text);
                    binding.edittextFolderAddInputName.setSelection(text.length());
                    binding.edittextFolderAddInputName.addTextChangedListener(this);
                }

                if (s.length() <= 32) {
                    binding.textviewFolderAddCount.setText(s.length() + "/32");
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }
                if (s.length() == 0) {
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    binding.buttonFolderAddCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonFolderAddCancel.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.edittextFolderAddShareInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    folderShareActivityModel.searchUserInfo(s.toString());

                    folderShareActivityModel.getUserSearchLiveData().observe(FolderAddActivity.this, userResponse -> {
                        if (userResponse != null) {
                            binding.textviewFolderAddShareSearch.setText(userResponse.getName());
                            profile = userResponse.getProfile();
                            if (profile != null) {
                                if (profile.startsWith("http")) {
                                    Glide.with(FolderAddActivity.this).load(profile).error(R.drawable.profile_user).into(binding.imageviewFolderAddShareSearch);
                                    newProfile = profile;
                                } else {
                                    imageRef = storage.getReference().child(profile);
                                    Glide.with(FolderAddActivity.this).load(ProfileImage.convertGsToHttps(profile)).error(R.drawable.profile_user).into(binding.imageviewFolderAddShareSearch);
                                    newProfile = ProfileImage.convertGsToHttps(profile);
                                }
                            } else {
                                Glide.with(FolderAddActivity.this).load(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewFolderAddShareSearch);
                                newProfile = null;
                            }
                        }
                    });
                }

                if (s.length() == 0) {
                    binding.buttonFolderShareShareCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonFolderShareShareCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.constraintlayoutFolderAddShareSearch.setOnClickListener(v -> {
            if (!binding.textviewFolderAddShareSearch.getText().toString().isEmpty()) {
                showBottomSheetDialog();
                code = binding.edittextFolderAddShareInputName.getText().toString();
            } else {
                Toast.makeText(this, "사용자를 검색해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonFolderAddCancel.setOnClickListener(v -> binding.edittextFolderAddInputName.setText(""));

        binding.buttonFolderShareShareCancel.setOnClickListener(v -> binding.edittextFolderAddShareInputName.setText(""));

    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_folder_share_authority, null);
        bottomSheetDialog.setContentView(dialogView);

        RelativeLayout readButton = dialogView.findViewById(R.id.relative_layout_folder_share_read);
        RelativeLayout writeButton = dialogView.findViewById(R.id.relative_layout_folder_share_write);

        readButton.setOnClickListener(v -> {
            // 읽기 권한
            onClickDialogBtn("r");
            bottomSheetDialog.dismiss();
        });
        writeButton.setOnClickListener(v -> {
            // 쑈기 권한
            onClickDialogBtn("w");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    private void onClickDialogBtn(String role) {
        FolderAddItem newItem = new FolderAddItem(newProfile, code, role);
        itemList.add(newItem);
        adapter.notifyItemInserted(itemList.size() - 1);
        binding.edittextFolderAddShareInputName.setText("");
        Glide.with(this).load(R.drawable.profile_user).error(R.drawable.profile_user).into(binding.imageviewFolderAddShareSearch);
        binding.textviewFolderAddShareSearch.setText("");
        newProfile = null;

    }

}