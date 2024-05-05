package team.y2k2.globa.main.folder.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderNameEditBinding;

public class FolderNameEditActivity extends AppCompatActivity {

    ActivityFolderNameEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        binding = ActivityFolderNameEditBinding.inflate(getLayoutInflater());

        String folderName = intent.getStringExtra("folderName");

        binding.textviewFolderNameTitle.setText(folderName);

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

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.textviewFolderNameChangeConfirm.setOnClickListener(v -> {
            if(binding.textviewFolderNameChangeConfirm.getText().length() == 0) return;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("changedFolderName", binding.edittextFolderNameInputname.getText().toString()); // 결과 데이터 설정
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
