package team.y2k2.globa.main.profile.alert;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;

import team.y2k2.globa.api.clients.UserApiClient;
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

        setupRecyclerView();
        observeViewModel();

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.constraintlayoutAlert.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.constraintlayoutAlert.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            binding.constraintlayoutAlert.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });

        viewModel.setApiClient(new UserApiClient(this));
        viewModel.setUserId(getIntent().getStringExtra("userId"));
    }

    private void setupRecyclerView() {
        adapter = new AlertItemAdapter(new ArrayList<>(), () -> {
            viewModel.checkForChanges();
        });
        binding.recyclerviewAlert.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewAlert.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAlertLiveData().observe(this, alertResponse -> {
            if (alertResponse != null) {
                viewModel.setAlertStatus(alertResponse);
                adapter.setItems(viewModel.getAlertItems());
            }
        });

        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish != null && shouldFinish) {
                finish();
                Toast.makeText(this, "알림 설정이 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}