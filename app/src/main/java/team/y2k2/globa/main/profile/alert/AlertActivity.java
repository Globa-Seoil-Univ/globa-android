package team.y2k2.globa.main.profile.alert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityAlertBinding;

public class AlertActivity extends AppCompatActivity {

    ActivityAlertBinding binding;

    AlertViewModel viewModel;

    private String userId;
    private boolean uploadNofi, shareNofi, eventNofi;
    private boolean newUploadNofi, newShareNofi, newEventNofi;

    private ArrayList<AlertItem> alertItems = new ArrayList<>();

    private SharedPreferences nofiPref;
    private SharedPreferences.Editor nofiEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        Log.d(getClass().getSimpleName(), "userId: " + userId);

        viewModel = new ViewModelProvider(this).get(AlertViewModel.class);

        viewModel.getMyAlertStatus(userId);
        viewModel.getAlertLiveData().observe(this, alertResponse -> {
            if(alertResponse != null) {
                uploadNofi = alertResponse.isUploadNofi();
                shareNofi = alertResponse.isShareNofi();
                eventNofi = alertResponse.isEventNofi();
                Log.d(getClass().getSimpleName(), "업로드 알림: " + uploadNofi + ", 공유 알림: " + shareNofi + ", 이벤트 알림: " + eventNofi);
            }

            loadToggleList();

            AlertItemAdapter adapter = new AlertItemAdapter(alertItems, this);

            binding.recyclerviewAlert.setAdapter(adapter);
            binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        });

        binding.imagebuttonAlertBack.setOnClickListener(v -> {
            Log.d(getClass().getSimpleName(), "뒤로가기 버튼 클릭 newUploadNofi: " + newUploadNofi + ", newShareNofi: " + newShareNofi + ", newEventNofi: " + newEventNofi);

            viewModel.requestAlertStatus(userId, newUploadNofi, newShareNofi, newEventNofi);

            finish();
        });

    }

    private void loadToggleList() {
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, uploadNofi));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, shareNofi));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, eventNofi));
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
