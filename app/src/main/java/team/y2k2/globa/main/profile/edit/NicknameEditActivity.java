package team.y2k2.globa.main.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import team.y2k2.globa.api.model.request.LoginRequest;

public class NicknameEditActivity extends AppCompatActivity {

    ActivityNicknameEditBinding binding;
    private NicknameEditViewModel nicknameEditViewModel;
    boolean isChanged = false;
    private String userId;
    private String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNicknameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nicknameEditViewModel = new ViewModelProvider(this).get(NicknameEditViewModel.class);
        userId = getIntent().getStringExtra("userId");
        currentName = getIntent().getStringExtra("current_name");

        initializeUI(userId, currentName);
    }

    private void initializeUI(String userId, String currentName) {

        binding.buttonNicknameeditBack.setOnClickListener(v -> {
            finish();
        });

        binding.edittextNicknameeditInputname.setText(currentName);
        int currentNameCount = binding.edittextNicknameeditInputname.getText().length();
        binding.textviewNicknameeditCount.setText(currentNameCount + "/32");

        binding.edittextNicknameeditInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전
                binding.textviewNicknameeditChange.setTextColor(Color.GRAY);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 변경
                int color = ContextCompat.getColor(NicknameEditActivity.this, R.color.primary);
                binding.textviewNicknameeditChange.setTextColor(color);
                binding.textviewNicknameeditCount.setText(s.length() + "/32");
                isChanged = true;
                if(s.length() == 0) {
                    binding.buttonNicknameEditCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonNicknameEditCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후
                if(s.length() > 32) {
                    binding.edittextNicknameeditInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextNicknameeditInputname.setText(text);
                    binding.edittextNicknameeditInputname.setSelection(text.length());
                    binding.edittextNicknameeditInputname.addTextChangedListener(this);
                }
                if(s.length() <= 32) {
                    binding.textviewNicknameeditCount.setText(s.length() + "/32");
                    binding.textviewNicknameeditChange.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }
                if(s.length() == 0) {
                    binding.textviewNicknameeditChange.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

        // 변경 버튼
        binding.textviewNicknameeditChange.setOnClickListener(v -> {
            if(isChanged) {
                if(binding.edittextNicknameeditInputname.getText().toString().isEmpty()) {
                    Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newNickname = binding.edittextNicknameeditInputname.getText().toString();
                nicknameEditViewModel.updateNickname(this,userId, newNickname);
                onNicknameChanged(newNickname);
            } else {
                Toast.makeText(this, "이름이 변경되지 않았습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 글자 모두 지우기 버튼
        binding.buttonNicknameEditCancel.setOnClickListener(v -> {
            if(binding.edittextNicknameeditInputname.getText().toString().equals("")){
                binding.buttonNicknameEditCancel.setVisibility(View.GONE);
            } else {
                binding.edittextNicknameeditInputname.setText("");
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