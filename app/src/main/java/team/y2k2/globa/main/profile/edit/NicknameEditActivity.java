package team.y2k2.globa.main.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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
    private LoginRequest loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNicknameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nicknameEditViewModel = new ViewModelProvider(this).get(NicknameEditViewModel.class);
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");
        String userId = preferences.getString("userId","");

        String currentName = getIntent().getStringExtra("current_name");
        binding.edittextNicknameeditInputname.setText(currentName);


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
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후
            }
        });

        // 이름이 변경되었을때 동작
        binding.textviewNicknameeditChange.setOnClickListener(v -> {
            if(isChanged) {
                String newNickname = binding.edittextNicknameeditInputname.getText().toString();
                nicknameEditViewModel.updateNickname(this,userId, authorization, newNickname);
                onNicknameChanged(newNickname);
            }
        });

        binding.buttonNicknameeditBack.setOnClickListener(v -> {
            finish();
        });

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