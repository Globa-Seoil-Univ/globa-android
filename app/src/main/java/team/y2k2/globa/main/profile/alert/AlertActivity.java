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

        viewModel = new ViewModelProvider(this).get(AlertViewModel.class);

        loadNofiPref();

        loadToggleList();

        userId = getIntent().getStringExtra("userId");
        Log.d(getClass().getSimpleName(), "userId: " + userId);

        AlertItemAdapter adapter = new AlertItemAdapter(alertItems, this);

        binding.recyclerviewAlert.setAdapter(adapter);
        binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        binding.imagebuttonAlertBack.setOnClickListener(v -> {
            Log.d(getClass().getSimpleName(), "뒤로가기 버튼 클릭 newUploadNofi: " + newUploadNofi + ", newShareNofi: " + newShareNofi + ", newEventNofi: " + newEventNofi);

            setNofiPref();

            viewModel.requestAlertStatus(userId, newUploadNofi, newShareNofi, newEventNofi);

            finish();
        });

    }

    private void loadToggleList() {
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, uploadNofi));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, shareNofi));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, eventNofi));
    }

    private void loadNofiPref() {
        nofiPref = getSharedPreferences("alert", MODE_PRIVATE);
        nofiEditor = nofiPref.edit();
        uploadNofi = nofiPref.getBoolean("uploadNofi", false);
        shareNofi = nofiPref.getBoolean("shareNofi", false);
        eventNofi = nofiPref.getBoolean("eventNofi", false);
        Log.d(getClass().getSimpleName(), "Pref 로드 (uploadNofi: " + uploadNofi + ", shareNofi: " + shareNofi + ", eventNofi: " + eventNofi + ")");
    }

    private void setNofiPref() {
        nofiEditor.putBoolean("uploadNofi", newUploadNofi);
        nofiEditor.putBoolean("shareNofi", newShareNofi);
        nofiEditor.putBoolean("eventNofi", newEventNofi);
        nofiEditor.apply();
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
