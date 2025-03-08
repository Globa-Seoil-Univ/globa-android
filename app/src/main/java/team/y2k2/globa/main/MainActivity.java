package team.y2k2.globa.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import team.y2k2.globa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_RECORD = 101;
    private static final int REQUEST_CODE_UPLOAD_RECORD = 102;
    private ActivityMainBinding binding;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.setActivity(this);
        viewModel.handleUserFcmToken();

        // 유닛 테스트 및 통신 결과 확인을 위해 로그 출력 - 유저 AccessToken
        Log.i(getClass().getName(), viewModel.getUserAccessToken());

        setContentView(binding.getRoot());
        setNavigationView(binding.navigationMainBottom);
    }

    private void setNavigationView(BottomNavigationView view) {
        view.setOnItemSelectedListener(item -> {
            viewModel.viewFragment(item.getItemId());
            return true;
        });
    }

    /**
     * 화면 외부 호출 결과 핸들링
     * 1. 업로드 버튼 -> 음성 파일 path 정보 가져옴
     * 2. 업로드 폼 제출 -> 음성 파일 API Request
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPLOAD_RECORD && resultCode == RESULT_OK) {
            viewModel.mainFragment.showRecords(0);
        }
        if (requestCode == REQUEST_CODE_PICK_RECORD && resultCode == RESULT_OK) {
            viewModel.uploadRecord(data);
        }
    }

}

