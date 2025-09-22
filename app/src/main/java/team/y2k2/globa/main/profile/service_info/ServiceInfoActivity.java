package team.y2k2.globa.main.profile.service_info;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.databinding.ActivityServiceInfoBinding;

public class ServiceInfoActivity extends AppCompatActivity {
    private ActivityServiceInfoBinding binding;
    private ServiceInfoViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ServiceInfoViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.layoutServiceInfo.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.layoutServiceInfo.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            binding.layoutServiceInfo.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });

        initAdapter();
        observeViewModel();
    }

    private void initAdapter() {
        ServiceInfoItemAdapter adapter = new ServiceInfoItemAdapter(this, viewModel.getServiceInfoItems());
        binding.recyclerviewItemService.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                finish();
            }
        });
    }
}