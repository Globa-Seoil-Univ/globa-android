package team.y2k2.globa.main.folder.share;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import team.y2k2.globa.api.model.request.FolderShareAddRequest;
import team.y2k2.globa.databinding.ActivityFolderShareBinding;

public class FolderShareActivity extends AppCompatActivity {

    ActivityFolderShareBinding binding;
    private FolderShareViewModel folderShareViewModel;
    boolean isSearched = false;
    private FolderShareAdapter adapter;
    private List<FolderShareItem> itemList = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference profileImageRef;
    private String lastImageUrl;
    private int tempUserId;

    private SharedPreferences preferences;
    int folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("folderid", MODE_PRIVATE);
        folderId = preferences.getInt("folderid", 0);

        // 코드 입력하는 EditText가 비어있을 땐 X버튼 안보이게 하기
        String input = binding.edittextFoldershareInputname.getText().toString();
        if(input.equals("")) {
            binding.buttonFoldershareCancel.setWidth(0);
        }

        // 뒤로가기 버튼
        binding.buttonFoldershareBack.setOnClickListener(v -> {
            finish();
        });

        // 뷰모델 가져오기
        folderShareViewModel = new ViewModelProvider(this).get(FolderShareViewModel.class);

        adapter = new FolderShareAdapter(itemList);
        binding.recyclerviewFoldershareSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewFoldershareSelected.setAdapter(adapter);

        binding.edittextFoldershareInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6) {
                    folderShareViewModel.searchUserInfo(s.toString());

                    folderShareViewModel.getUserSearchLiveData().observe(FolderShareActivity.this, userInfoResponse -> {
                        if(userInfoResponse != null) {
                            binding.textviewFoldershareSearch.setText(userInfoResponse.getName());
                            profileImageRef = storage.getReference().child(userInfoResponse.getProfile());
                            String firebaseImageUrl = profileImageRef.toString();
                            lastImageUrl = firebaseImageUrl;
                            Glide.with(FolderShareActivity.this).load(lastImageUrl)
                                    .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                                    .into(binding.imageviewFoldershareSearch);
                            tempUserId = userInfoResponse.getUserId();
                            isSearched = true;
                        }
                    });
                    folderShareViewModel.getErrorLiveData().observe(FolderShareActivity.this, errorMessage -> {
                        Toast.makeText(FolderShareActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.constraintlayoutFoldershareSearch.setOnClickListener(v -> {

            showBottomSheetDialog();

        });

        binding.textviewFoldershareConfirm.setOnClickListener(v -> {

            // 리사이클러뷰를 순회하면서 API 전송
            LinearLayoutManager collectNewUserManager = (LinearLayoutManager) binding.recyclerviewFoldershareSelected.getLayoutManager();
            if(collectNewUserManager != null) {
                int firstPosition = collectNewUserManager.findFirstVisibleItemPosition();
                int lastPosition = collectNewUserManager.findLastVisibleItemPosition();

                for(int i = firstPosition; i <= lastPosition; i++) {
                    RecyclerView.ViewHolder viewHolder = binding.recyclerviewFoldershareSelected.findViewHolderForAdapterPosition(i);
                    if(viewHolder instanceof FolderShareAdapter.MyViewHolder) {
                        FolderShareAdapter.MyViewHolder adapterViewHolder = (FolderShareAdapter.MyViewHolder) viewHolder;
                        FolderShareItem item = adapter.getItem(i);

                        int userId = item.getUserId();
                        String role = item.getRole();
                        FolderShareAddRequest.setRole(role);

                        folderShareViewModel.addSharedUser(folderId, userId);
                    }
                }
            }

        });

    }

    protected void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_foldershare_authority, null);
        bottomSheetDialog.setContentView(dialogView);

        Button readButton = dialogView.findViewById(R.id.relativelayout_foldershare_read);
        Button writeButton = dialogView.findViewById(R.id.relativelayout_foldershare_write);

        readButton.setOnClickListener(v -> {
            if(lastImageUrl != null) {
                FolderShareItem newItem = new FolderShareItem(lastImageUrl);
                newItem.setUserId(tempUserId);
                newItem.setRole("r");
                itemList.add(newItem);
                adapter.notifyItemInserted(itemList.size() - 1);
                binding.edittextFoldershareInputname.setText("");
                Glide.with(this).load(R.mipmap.ic_launcher)
                        .into(binding.imageviewFoldershareSearch);
                binding.textviewFoldershareSearch.setText("");
                lastImageUrl = null;
            }
        });
        writeButton.setOnClickListener(v -> {
            if(lastImageUrl != null) {
                FolderShareItem newItem = new FolderShareItem(lastImageUrl);
                newItem.setUserId(tempUserId);
                newItem.setRole("w");
                itemList.add(newItem);
                adapter.notifyItemInserted(itemList.size() - 1);
                binding.edittextFoldershareInputname.setText("");
                Glide.with(this).load(R.mipmap.ic_launcher)
                        .into(binding.imageviewFoldershareSearch);
                binding.textviewFoldershareSearch.setText("");
                lastImageUrl = null;
            }
        });

    }

}