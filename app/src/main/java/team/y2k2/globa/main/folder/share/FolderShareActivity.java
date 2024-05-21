package team.y2k2.globa.main.folder.share;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 코드 입력하는 EditText가 비어있을 땐 X버튼 안보이게 하기
        String input = binding.edittextFoldershareInputname.getText().toString();
        if(input.equals("")) {
            binding.buttonFoldershareCancel.setWidth(0);
        }

        binding.layoutFoldershareDropbox.setOnClickListener(v -> {
            showAlertDialog();
        });

        folderShareViewModel = new ViewModelProvider(this).get(FolderShareViewModel.class);
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");

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
                    folderShareViewModel.fetchUserInfo(authorization, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        folderShareViewModel.getUserInfoLiveData().observe(this, userInfoResponse -> {
            if(userInfoResponse != null) {
                binding.textviewFoldershareSearch.setText(userInfoResponse.getName());
                profileImageRef = storage.getReference().child(userInfoResponse.getProfile());
                String firebaseImageUrl = profileImageRef.toString();
                lastImageUrl = firebaseImageUrl;
                Glide.with(this).load(firebaseImageUrl)
                        .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                        .into(binding.imageviewFoldershareSearch);
                isSearched = true;
            }
        });
        folderShareViewModel.getErrorLiveData().observe(this, errorMessage -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });

        binding.constraintlayoutFoldershareSearch.setOnClickListener(v -> {
            if(lastImageUrl != null) {
                FolderShareItem newItem = new FolderShareItem(lastImageUrl);
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

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_foldershare_authority, null);
        builder.setView(dialogView);

        RelativeLayout readButton = dialogView.findViewById(R.id.relativelayout_foldershare_read);
        RelativeLayout wirteButton = dialogView.findViewById(R.id.relativelayout_foldershare_write);

        AlertDialog dialog = builder.create();
        dialog.show();

        readButton.setOnClickListener(v -> {
            // 읽기 권한 클릭 시 작동
        });
        wirteButton.setOnClickListener(v -> {
            // 편집 권한 클릭 시 작동
        });
    }

}