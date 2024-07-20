package team.y2k2.globa.main.profile.alert;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityAlertBinding;

public class AlertActivity extends AppCompatActivity {

    ActivityAlertBinding binding;

    AlertModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new AlertModel();
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlertItemAdapter adapter = new AlertItemAdapter(model.getItems());

        binding.recyclerviewAlert.setAdapter(adapter);
        binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }
}
