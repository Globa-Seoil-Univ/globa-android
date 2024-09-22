package team.y2k2.globa.main.profile.service_info;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityServiceInfoBinding;

public class ServiceInfoActivity extends AppCompatActivity {
    ActivityServiceInfoBinding binding;
    ServiceInfoModel model;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ServiceInfoModel();
        binding = ActivityServiceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageviewServiceBack.setOnClickListener(v -> {
            finish();
        });

        ServiceInfoItemAdapter adapter = new ServiceInfoItemAdapter(model.getItems());

        binding.recyclerviewItemService.setAdapter(adapter);
        binding.recyclerviewItemService.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

    }
}
