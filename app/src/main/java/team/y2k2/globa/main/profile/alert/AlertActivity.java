package team.y2k2.globa.main.profile.alert;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import team.y2k2.globa.databinding.ActivityAlertBinding;

public class AlertActivity extends AppCompatActivity {

    private ActivityAlertBinding binding;
    private AlertViewModel viewModel;
    private AlertItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AlertViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setUserId(getIntent().getStringExtra("userId"));

        initAdapter();
        observeViewModel();

        deleteFCMToken();
    }

    private void initAdapter() {
        adapter = new AlertItemAdapter(viewModel.getAlertItems(), this);
        binding.recyclerviewAlert.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAlertLiveData().observe(this, alertResponse -> {
            if (alertResponse != null) {
                viewModel.setAlertStatus(alertResponse);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                finish();
            }
        });
    }

    private void deleteFCMToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM 토큰", "FCM 토큰 삭제 완료");
            } else {
                Log.d("FCM 토큰", "FCM 토큰 삭제 실패");
            }
        });
    }
}