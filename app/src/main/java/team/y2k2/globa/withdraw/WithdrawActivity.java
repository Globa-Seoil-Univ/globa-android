package team.y2k2.globa.withdraw;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityWithdrawBinding;

public class WithdrawActivity extends AppCompatActivity {

    ActivityWithdrawBinding binding;
    private WithdrawViewModel withdrawViewModel;
    String content = ""; // 회원 탈퇴 사유(editText)를 담을 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        withdrawViewModel = new ViewModelProvider(this).get(WithdrawViewModel.class);

        initializeUI();

    }

    private void initializeUI() {
        binding.buttonWithdrawBack.setOnClickListener(v -> finish());
        ArrayList<String> reasonList = new ArrayList<>();

        reasonList.add("서비스 사용이 불편해요");
        reasonList.add("정확성이 떨어져요");
        reasonList.add("기능이 부족해요");
        reasonList.add("다른 서비스가 더 좋아요");

        for(String reason : reasonList) {
            RadioButton radioButton = new RadioButton(WithdrawActivity.this);
            radioButton.setText(reason);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            radioButton.setBackgroundResource(R.drawable.folder_move_radio_button);
            binding.groupWithdrawCheck.addView(radioButton);
        }

        // 회원 탈퇴 버튼 클릭 동작
        binding.buttonWithdrawWithdraw.setOnClickListener(v -> {
            content = binding.edittextWithdrawDetail.getText().toString();
            int surveyType = 1;

            // 회원 탈퇴 작동 (탈퇴 사유 전송, 데이터 삭제 등)
            withdrawViewModel.withdrawUser(surveyType, content, this);

        });

        withdrawViewModel.getResponseLiveData().observe(this, response -> {
            if(response == 200) {
                SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
            }
        });

    }

}