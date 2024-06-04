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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        binding = ActivityFolderNameEditBinding.inflate(getLayoutInflater());

        String folderTitle = intent.getStringExtra("folderTitle");

        binding.textviewFolderNameTitle.setText(folderTitle);

        folderNameEditViewModel = new ViewModelProvider(this).get(FolderNameEditViewModel.class);
        SharedPreferences preferences = getSharedPreferences("folderid", Activity.MODE_PRIVATE);
        int folderId = preferences.getInt("folderId", 0);

        binding.edittextFolderNameInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    binding.textviewFolderNameChangeConfirm.setTextColor(getResources().getColor(R.color.primary));
                }
                else {
                    binding.textviewFolderNameChangeConfirm.setTextColor(getResources().getColor(R.color.darkGray));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
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
        binding.buttonFolderNameBack.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });

        setContentView(binding.getRoot());
    }
}
