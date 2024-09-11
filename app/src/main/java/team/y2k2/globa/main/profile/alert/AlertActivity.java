package team.y2k2.globa.main.profile.alert;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityAlertBinding;

public class AlertActivity extends AppCompatActivity {

    ActivityAlertBinding binding;

    AlertModel model;
    AlertViewModel viewModel;

    private String userId;
    private boolean uploadNofi, shareNofi, eventNofi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new AlertModel();
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AlertViewModel.class);

        userId = getIntent().getStringExtra("userId");
        Log.d(getClass().getSimpleName(), "userId: " + userId);



        binding.imagebuttonAlertBack.setOnClickListener(v -> {
            // 알림 수정 API Request 필요
            viewModel.requestAlertStatus(userId, uploadNofi, shareNofi, eventNofi);

            finish();
        });

        AlertItemAdapter adapter = new AlertItemAdapter(model.getItems());

        binding.recyclerviewAlert.setAdapter(adapter);
        binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }
}
