package team.y2k2.globa.main.profile.edit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNicknameEditBinding;

public class NicknameEditActivity extends AppCompatActivity {

    ActivityNicknameEditBinding binding;
    boolean isChanged = false;
    private NicknameEditViewModel nicknameEditViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNicknameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nicknameEditViewModel = new ViewModelProvider(this).get(NicknameEditViewModel.class);
        String userId = getIntent().getStringExtra("userId");
        String currentName = getIntent().getStringExtra("current_name");

        initializeUI(userId, currentName);
    }

    private void initializeUI(String userId, String currentName) {
        binding.buttonNicknameEditBack.setOnClickListener(v -> finish());
        binding.edittextNicknameEditInputName.setText(currentName);
        int currentNameCount = binding.edittextNicknameEditInputName.getText().length();
        binding.textviewNicknameEditCount.setText(currentNameCount + "/32");
        binding.edittextNicknameEditInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전
                binding.textviewNicknameEditChange.setTextColor(Color.GRAY);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 변경
                int color = ContextCompat.getColor(NicknameEditActivity.this, R.color.primary);
                binding.textviewNicknameEditChange.setTextColor(color);
                binding.textviewNicknameEditCount.setText(s.length() + "/32");
                isChanged = true;
                if (s.length() == 0) {
                    binding.buttonNicknameEditCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonNicknameEditCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후
                if (s.length() > 32) {
                    binding.edittextNicknameEditInputName.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextNicknameEditInputName.setText(text);
                    binding.edittextNicknameEditInputName.setSelection(text.length());
                    binding.edittextNicknameEditInputName.addTextChangedListener(this);
                }
                if (s.length() <= 32) {
                    binding.textviewNicknameEditCount.setText(s.length() + "/32");
                    binding.textviewNicknameEditChange.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }
                if (s.length() == 0) {
                    binding.textviewNicknameEditChange.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

        // 변경 버튼
        binding.textviewNicknameEditChange.setOnClickListener(v -> {
            if (isChanged) {
                if (binding.edittextNicknameEditInputName.getText().toString().isEmpty()) {
                    Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newNickname = binding.edittextNicknameEditInputName.getText().toString();
                nicknameEditViewModel.updateNickname(this, userId, newNickname);
                onNicknameChanged(newNickname);
            } else {
                Toast.makeText(this, "이름이 변경되지 않았습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 글자 모두 지우기 버튼
        binding.buttonNicknameEditCancel.setOnClickListener(v -> {
            if (binding.edittextNicknameEditInputName.getText().toString().isEmpty()) {
                binding.buttonNicknameEditCancel.setVisibility(View.GONE);
            } else {
                binding.edittextNicknameEditInputName.setText("");
            }
        });
    }

    private void onNicknameChanged(String newNickname) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_name", newNickname);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}