package team.y2k2.globa.main.folder.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;
import team.y2k2.globa.databinding.ActivityFolderNameEditBinding;

public class FolderNameEditActivity extends AppCompatActivity {

    ActivityFolderNameEditBinding binding;

    private FolderNameEditViewModel folderNameEditViewModel;

    int folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderNameEditBinding.inflate(getLayoutInflater());

        initializeUI();

        setContentView(binding.getRoot());
    }

    private void initializeUI() {

        // 뷰모델 로드
        folderNameEditViewModel = new ViewModelProvider(this).get(FolderNameEditViewModel.class);

        // folderId 받아오기
        folderId = getIntent().getIntExtra("folderId", 0);
        if(folderId == 0) {
            Toast.makeText(this, "유효하지 않은 폴더 ID 입니다", Toast.LENGTH_SHORT).show();
            return;
        }

        initializeEditText();

        initializeButton();
    }

    private void initializeEditText() {
        binding.edittextFolderNameInputname.setText(getIntent().getStringExtra("folderTitle"));
        binding.textviewFolderNameCount.setText(binding.edittextFolderNameInputname.getText().toString().length() + "/32");
        // EditText에 TextWather적용
        binding.edittextFolderNameInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                } else {
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                binding.textviewFolderNameCount.setText(s.length() + "/32");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewFolderNameCount.setText(s.length() + "/32");
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 32) {
                    binding.edittextFolderNameInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextFolderNameInputname.setText(text);
                    binding.edittextFolderNameInputname.setSelection(text.length());
                    binding.edittextFolderNameInputname.addTextChangedListener(this);
                }

                if (s.length() <= 32) {
                    binding.textviewFolderNameCount.setText(s.length() + "/32");
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }

                if (s.length() == 0) {
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });
    }

    private void initializeButton() {
        // 확인 버튼
        binding.textviewFolderNameChangeConfirm.setOnClickListener(v -> {
            if(binding.edittextFolderNameInputname.getText().length() == 0) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = binding.edittextFolderNameInputname.getText().toString();
            FolderNameEditRequest folderNameEditRequest = new FolderNameEditRequest(title);

            folderNameEditViewModel.folderRename(folderId, folderNameEditRequest);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", binding.edittextFolderNameInputname.getText().toString()); // 결과 데이터 설정
            setResult(RESULT_OK, resultIntent);
            finish(); // 액티비티 종료
        });

        // 뒤로가기 버튼
        binding.buttonFolderNameBack.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });
    }

}
