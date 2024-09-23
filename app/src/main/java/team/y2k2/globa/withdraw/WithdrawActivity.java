package team.y2k2.globa.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import team.y2k2.globa.databinding.ActivityWithdrawBinding;
import team.y2k2.globa.intro.IntroActivity;

public class WithdrawActivity extends AppCompatActivity {

    ActivityWithdrawBinding binding;
    private WithdrawViewModel withdrawViewModel;
    String content = ""; // 회원 탈퇴 사유(editText)를 담을 변수
    ArrayList<Integer> checkedIndexs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        withdrawViewModel = new ViewModelProvider(this).get(WithdrawViewModel.class);

        initializeUI();

    }

    private void initializeUI() {

        binding.buttonWithdrawBack.setOnClickListener(v -> {
            finish();
        });

        checkedIndexs = new ArrayList<>();

        // 회원 탈퇴 버튼 클릭 동작
        binding.buttonWithdrawWithdraw.setOnClickListener(v -> {

            // 회원 탈퇴 사유 수집
            if(binding.checkboxWithdrawReason1.isChecked()) {
                checkedIndexs.add(1);
            }
            if(binding.checkboxWithdrawReason2.isChecked()) {
                checkedIndexs.add(2);
            }
            if(binding.checkboxWithdrawReason3.isChecked()) {
                checkedIndexs.add(3);
            }
            if(binding.checkboxWithdrawReason4.isChecked()) {
                checkedIndexs.add(4);
            }
            content = binding.edittextWithdrawDetail.getText().toString();

            int[] surveyType = new int[checkedIndexs.size()];
            for(int i = 0; i < checkedIndexs.size(); i++) {
                surveyType[i] = checkedIndexs.get(i);
            }

            // 회원 탈퇴 작동 (탈퇴 사유 전송, 데이터 삭제 등)
            withdrawViewModel.withdrawUser(surveyType, content, this);

        });

    }

}