package team.y2k2.globa.main.folder.rename;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderRenameBinding;

public class FolderRenameActivity extends AppCompatActivity {

    ActivityFolderRenameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderRenameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("Key");
        binding.edittextFolderrenameInputname.setText(name);

        binding.buttonFolderrenameCancel.setOnClickListener(v -> {
            binding.edittextFolderrenameInputname.setText("");
        });

        binding.buttonFolderrenameBack.setOnClickListener(v -> {
            finish();
        });

        binding.edittextFolderrenameInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 입력전
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트가 변경될 때마다 호출
                if(s.length() > 0) {
                    int textColor = ContextCompat.getColor(FolderRenameActivity.this, R.color.primary);
                    binding.textviewFolderrenameChange.setTextColor(textColor);
                } else {
                    binding.textviewFolderrenameChange.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}