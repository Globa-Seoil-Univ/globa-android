package team.y2k2.globa.withdraw;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityWithdrawBinding;

public class WithdrawActivity extends AppCompatActivity {

    ActivityWithdrawBinding binding;
    private ArrayList<String> selectedItems = new ArrayList<>(); // 회원 탈퇴 사유를 담을 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 회원 탈퇴 버튼 클릭 동작
        binding.buttonWithdrawWithdraw.setOnClickListener(v -> {

            // 회원 탈퇴 사유 수집
            selectedItems.clear();
            if(binding.checkboxWithdrawReason1.isChecked()) {
                selectedItems.add(binding.checkboxWithdrawReason1.getText().toString());
            }
            if(binding.checkboxWithdrawReason2.isChecked()) {
                selectedItems.add(binding.checkboxWithdrawReason2.getText().toString());
            }
            if(binding.checkboxWithdrawReason3.isChecked()) {
                selectedItems.add(binding.checkboxWithdrawReason3.getText().toString());
            }
            if(binding.checkboxWithdrawReason4.isChecked()) {
                selectedItems.add(binding.checkboxWithdrawReason4.getText().toString());
            }
            selectedItems.add(binding.edittextWithdrawDetail.getText().toString()); // 기타 사유 수집

            // 회원 탈퇴 작동 (탈퇴 사유 전송, 데이터 삭제 등)

        });


    }
}