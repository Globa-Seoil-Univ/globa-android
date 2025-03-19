package team.y2k2.globa.main.profile.alert;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityAlertBinding;

public class AlertActivity extends AppCompatActivity {

    private final ArrayList<AlertItem> alertItems = new ArrayList<>();
    ActivityAlertBinding binding;
    AlertViewModel viewModel;
    private String userId;
    private boolean uploadNotification, shareNotification, eventNotification;
    private boolean newUploadNotification, newShareNotification, newEventNotification;
    private AlertItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM 토큰", "FCM 토큰 삭제 완료");
            } else {
                Log.d("FCM 토큰", "FCM 토큰 삭제 실패");
            }
        });

        userId = getIntent().getStringExtra("userId");
        Log.d(getClass().getSimpleName(), "userId: " + userId);

        viewModel = new ViewModelProvider(this).get(AlertViewModel.class);

        viewModel.getMyAlertStatus(userId);
        viewModel.getAlertLiveData().observe(this, alertResponse -> {
            if (alertResponse != null) {
                uploadNotification = alertResponse.isUploadNofi();
                shareNotification = alertResponse.isShareNofi();
                eventNotification = alertResponse.isEventNofi();
                Log.d(getClass().getSimpleName(), "업로드 알림: " + uploadNotification + ", 공유 알림: " + shareNotification + ", 이벤트 알림: " + eventNotification);
            }

            loadToggleList();

            adapter = new AlertItemAdapter(alertItems, this);

            binding.recyclerviewAlert.setAdapter(adapter);
            binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        });

        binding.imageButtonAlertBack.setOnClickListener(v -> {

            newUploadNotification = adapter.isUploadChecked();
            newShareNotification = adapter.isShareChecked();
            newEventNotification = adapter.isEventChecked();

            Log.d(getClass().getSimpleName(), "뒤로가기 버튼 클릭 newUploadNotification: " + newUploadNotification + ", newShareNotification: " + newShareNotification + ", newEventNotification: " + newEventNotification);

            viewModel.requestAlertStatus(userId, newUploadNotification, newShareNotification, newEventNotification);

            finish();
        });

    }

    private void loadToggleList() {
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, uploadNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, shareNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, eventNotification));
    }

    public void setNewUploadNotification(boolean newUploadNotification) {
        Log.d(getClass().getSimpleName(), "newUploadNotification setter 작동");
        this.newUploadNotification = newUploadNotification;
    }

    public void setNewShareNotification(boolean newShareNotification) {
        Log.d(getClass().getSimpleName(), "newShareNotification setter 작동");
        this.newShareNotification = newShareNotification;
    }

    public void setNewEventNotification(boolean newEventNotification) {
        Log.d(getClass().getSimpleName(), "newEventNotification setter 작동");
        this.newEventNotification = newEventNotification;
    }
}
