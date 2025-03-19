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

    ActivityAlertBinding binding;

    AlertViewModel viewModel;

    private String userId;
    private boolean uploadNotification, shareNotification, eventNotification;
    private boolean newUploadNofi, newShareNofi, newEventNofi;

    private final ArrayList<AlertItem> alertItems = new ArrayList<>();

    private AlertItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
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
            if(alertResponse != null) {
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

            newUploadNofi = adapter.isUploadChecked();
            newShareNofi = adapter.isShareChecked();
            newEventNofi = adapter.isEventChecked();

            Log.d(getClass().getSimpleName(), "뒤로가기 버튼 클릭 newUploadNofi: " + newUploadNofi + ", newShareNofi: " + newShareNofi + ", newEventNofi: " + newEventNofi);

            viewModel.requestAlertStatus(userId, newUploadNofi, newShareNofi, newEventNofi);

            finish();
        });

    }

    private void loadToggleList() {
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, uploadNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, shareNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, eventNotification));
    }

    public void setNewUploadNofi(boolean newUploadNofi) {
        Log.d(getClass().getSimpleName(), "newUploadNofi setter 작동");
        this.newUploadNofi = newUploadNofi;
    }
    public void setNewShareNofi(boolean newShareNofi) {
        Log.d(getClass().getSimpleName(), "newShareNofi setter 작동");
        this.newShareNofi = newShareNofi;
    }
    public void setNewEventNofi(boolean newEventNofi) {
        Log.d(getClass().getSimpleName(), "newEventNofi setter 작동");
        this.newEventNofi = newEventNofi;
    }
}
