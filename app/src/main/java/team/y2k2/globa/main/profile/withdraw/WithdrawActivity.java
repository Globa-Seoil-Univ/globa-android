package team.y2k2.globa.main.profile.withdraw; // 본인의 패키지 경로로 수정

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // View import 추가
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R; // 리소스 경로 수정
import team.y2k2.globa.databinding.ActivityWithdrawBinding; // 생성된 바인딩 클래스

public class WithdrawActivity extends AppCompatActivity {

    private ActivityWithdrawBinding binding;
    private WithdrawViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw);

        WithdrawViewModelFactory factory = new WithdrawViewModelFactory(this); // DI 사용 시 수정
        viewModel = new ViewModelProvider(this, factory).get(WithdrawViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupUIListeners();
        setupObservers(); // Observer 설정 호출
    }

    private void setupUIListeners() {
        binding.buttonWithdrawBack.setOnClickListener(v -> finish());

        binding.groupWithdrawCheck.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            if (checkedRadioButton != null) {
                viewModel.setSelectedReason(checkedRadioButton.getText().toString());
            } else {
                viewModel.setSelectedReason(null);
            }
        });
    }

    /** ViewModel의 LiveData 관찰자 설정 */
    private void setupObservers() {
        // 로딩 상태 관찰 -> ProgressBar visibility 제어
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                binding.progressbarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                // 로딩 중일 때 다른 UI 요소 비활성화 (선택 사항)
                binding.buttonWithdrawWithdraw.setEnabled(!isLoading && viewModel.isWithdrawButtonEnabled.getValue() != null && viewModel.isWithdrawButtonEnabled.getValue());
                binding.groupWithdrawCheck.setEnabled(!isLoading);
                binding.edittextWithdrawDetail.setEnabled(!isLoading);
            }
        });

        // 탈퇴 버튼 활성화 상태 관찰 -> Button enabled 제어
        viewModel.isWithdrawButtonEnabled.observe(this, isEnabled -> {
            if (isEnabled != null) {
                // 로딩 중이 아닐 때만 활성화 상태 반영 (로딩 중에는 위 isLoading observer에서 제어)
                Boolean loading = viewModel.isLoading.getValue();
                if (loading == null || !loading) {
                    binding.buttonWithdrawWithdraw.setEnabled(isEnabled);
                }
            }
        });


        // --- 기존 Observer 들 ---

        // 토스트 메시지 이벤트 관찰
        viewModel.showToastEvent.observe(this, event -> {
            if (event != null) {
                String message = event.getContentIfNotHandled();
                if (message != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 탈퇴 성공 이벤트 관찰 (SharedPreferences 처리)
        viewModel.withdrawSuccessEvent.observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                clearUserSession();
            }
        });

        // 탈퇴 실패 이벤트 관찰
        viewModel.withdrawErrorEvent.observe(this, event -> {
            if (event != null) {
                String errorMessage = event.getContentIfNotHandled();
                if (errorMessage != null) {
                    Toast.makeText(this, "탈퇴 처리 중 오류 발생:\n" + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        // 내비게이션 이벤트 관찰
        viewModel.navigateToActivity.observe(this, event -> {
            if (event != null) {
                Class<?> targetActivity = event.getContentIfNotHandled();
                if (targetActivity != null) {
                    Intent intent = new Intent(this, targetActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    // 사용자 세션 정보 클리어 (변경 없음)
    private void clearUserSession() {
        try {
            SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Log.d(getClass().getSimpleName(), "SharedPreferences cleared successfully.");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error clearing SharedPreferences", e);
            Toast.makeText(this, "세션 정리 중 오류 발생", Toast.LENGTH_SHORT).show();
        }
    }
}