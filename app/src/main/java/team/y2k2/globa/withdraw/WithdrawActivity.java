package team.y2k2.globa.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.databinding.ActivityWithdrawBinding;
import team.y2k2.globa.intro.IntroActivity;

public class WithdrawActivity extends AppCompatActivity {

    ActivityWithdrawBinding binding;
    private WithdrawViewModel withdrawViewModel;

    int surveyType = 0; // 회원 탈퇴 사유(체크박스)를 담을 변수
    String content = ""; // 회원 탈퇴 사유(editText)를 담을 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        withdrawViewModel = new ViewModelProvider(this).get(WithdrawViewModel.class);
        /*
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer" + preferences.getString("accessToken", "");
         */


        // 회원 탈퇴 버튼 클릭 동작
        binding.buttonWithdrawWithdraw.setOnClickListener(v -> {

            // 회원 탈퇴 사유 수집
            if(binding.checkboxWithdrawReason1.isChecked()) {
                surveyType = Integer.parseInt(surveyType + "1");
            }
            if(binding.checkboxWithdrawReason2.isChecked()) {
                surveyType = Integer.parseInt(surveyType + "1");
            }
            if(binding.checkboxWithdrawReason3.isChecked()) {
                surveyType = Integer.parseInt(surveyType + "1");
            }
            if(binding.checkboxWithdrawReason4.isChecked()) {
                surveyType = Integer.parseInt(surveyType + "1");
            }
            content = binding.edittextWithdrawDetail.getText().toString();

            // 회원 탈퇴 작동 (탈퇴 사유 전송, 데이터 삭제 등)
            withdrawViewModel.withdrawUser(surveyType, content);

            Intent intent = new Intent(this, IntroActivity.class);
            Toast.makeText(this, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();

        });
    }
}