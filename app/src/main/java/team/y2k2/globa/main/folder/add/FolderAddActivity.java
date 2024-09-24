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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.ShareTarget;
import team.y2k2.globa.databinding.ActivityFolderAddBinding;
import team.y2k2.globa.main.ProfileImage;
import team.y2k2.globa.main.folder.share.FolderShareActivity;
import team.y2k2.globa.main.folder.share.FolderShareViewModel;

public class FolderAddActivity extends AppCompatActivity {

    ActivityFolderAddBinding binding;
    ApiClient apiClient;

    FolderShareViewModel folderShareViewModel;

    String profile;
    String newProfile;
    String code;

    FolderAddAdapter adapter;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageRef;

    private List<FolderAddItem> itemList = new ArrayList<>();
    private List<ShareTarget> shareTargetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        folderShareViewModel = new ViewModelProvider(this).get(FolderShareViewModel.class);

        apiClient = new ApiClient(this);

        adapter = new FolderAddAdapter(itemList, this);
        binding.recyclerviewFolderaddShareSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewFolderaddShareSelected.setAdapter(adapter);

        binding.buttonFolderaddBack.setOnClickListener(v -> {
            finish();
        });

        binding.textviewFolderAddConfirm.setOnClickListener(v -> {

            if(binding.edittextFolderaddInputname.getText().length() == 0) {
                Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if(adapter.getItemCount() != 0) {
                LinearLayoutManager collectNewUserManager = (LinearLayoutManager) binding.recyclerviewFolderaddShareSelected.getLayoutManager();
                if(collectNewUserManager != null) {
                    int firtPosition = collectNewUserManager.findFirstVisibleItemPosition();
                    int lastPosition = collectNewUserManager.findLastVisibleItemPosition();

                    for(int i = firtPosition; i <= lastPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = binding.recyclerviewFolderaddShareSelected.findViewHolderForAdapterPosition(i);
                        if(viewHolder instanceof FolderAddAdapter.MyViewHolder) {
                            FolderAddAdapter.MyViewHolder adapterViewHolder = (FolderAddAdapter.MyViewHolder) viewHolder;
                            FolderAddItem item = adapter.getItem(i);

                            String tempCode = item.getCode();
                            String tempRole = item.getRole();

                            shareTargetList.add(new ShareTarget(tempCode, tempRole));

                        }
                    }
                }
            }

            String title = binding.edittextFolderaddInputname.getText().toString();

            if (!shareTargetList.isEmpty()) {
                Log.d(getClass().getSimpleName(), "제목: " + title + ", 공유대상: " + shareTargetList.get(0).getCode() + ", " + shareTargetList.get(0).getRole());
            }

            apiClient.requestInsertFolder(title, shareTargetList);

            finish();

        });

        binding.edittextFolderaddInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewFolderaddCount.setText(s.length() + "/32");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    binding.textviewFolderaddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                } else {
                    binding.textviewFolderaddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 32) {
                    binding.edittextFolderaddInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextFolderaddInputname.setText(text);
                    binding.edittextFolderaddInputname.setSelection(text.length());
                    binding.edittextFolderaddInputname.addTextChangedListener(this);
                }

                if(s.length() <= 32) {
                    binding.textviewFolderaddCount.setText(s.length() + "/32");
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }
                if(s.length() == 0) {
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    binding.buttonFolderaddCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonFolderaddCancel.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.edittextFolderaddShareInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6) {
                    folderShareViewModel.searchUserInfo(s.toString());

                    folderShareViewModel.getUserSearchLiveData().observe(FolderAddActivity.this, userResponse -> {
                        if(userResponse != null) {
                            binding.textviewFolderaddShareSearch.setText(userResponse.getName());
                            profile = userResponse.getProfile();
                            if(profile != null) {
                                if(profile.startsWith("http")) {
                                    Glide.with(FolderAddActivity.this).load(profile)
                                            .error(R.drawable.profile_user)
                                            .into(binding.imageviewFolderaddShareSearch);
                                    newProfile = profile;
                                } else {
                                    imageRef = storage.getReference().child(profile);
                                    Glide.with(FolderAddActivity.this).load(ProfileImage.convertGsToHttps(profile))
                                            .error(R.drawable.profile_user)
                                            .into(binding.imageviewFolderaddShareSearch);
                                    newProfile = ProfileImage.convertGsToHttps(profile);
                                }
                            } else {
                                Glide.with(FolderAddActivity.this).load(R.drawable.profile_user)
                                        .error(R.drawable.profile_user)
                                        .into(binding.imageviewFolderaddShareSearch);
                                newProfile = null;
                            }
                        }
                    });
                }

                if(s.length() == 0) {
                    binding.buttonFolderaddShareCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonFolderaddShareCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.constraintlayoutFolderaddShareSearch.setOnClickListener(v -> {
            if(!binding.textviewFolderaddShareSearch.equals("")) {
                showBottomSheetDialog();
                code = binding.edittextFolderaddShareInputname.getText().toString();
            } else {
                Toast.makeText(this, "사용자를 검색해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonFolderaddCancel.setOnClickListener(v -> {
            binding.edittextFolderaddInputname.setText("");
        });

        binding.buttonFolderaddShareCancel.setOnClickListener(v -> {
            binding.edittextFolderaddShareInputname.setText("");
        });

    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_folder_share_authority, null);
        bottomSheetDialog.setContentView(dialogView);

        RelativeLayout readButton = dialogView.findViewById(R.id.relativelayout_foldershare_read);
        RelativeLayout writeButton = dialogView.findViewById(R.id.relativelayout_foldershare_write);

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
        binding.edittextFolderaddShareInputname.setText("");
        Glide.with(this).load(R.drawable.profile_user)
                .error(R.drawable.profile_user)
                .into(binding.imageviewFolderaddShareSearch);
        binding.textviewFolderaddShareSearch.setText("");
        newProfile = null;

    }

}