package team.y2k2.globa.main.profile.inquiry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.InquiryApiClient;
import team.y2k2.globa.databinding.ActivityInquiryBinding;

public class InquiryActivity extends AppCompatActivity {

    private ActivityInquiryBinding binding;
    private InquiryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(InquiryViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setInquiryApiClient(new InquiryApiClient()); // API 클라이언트 설정

        observeViewModel();
        initTextChangeListeners();
    }

    private void observeViewModel() {
        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                finish();
            }
        });

        viewModel.getShowToast().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                viewModel.doneShowToast();
            }
        });
    }

    private void initTextChangeListeners() {
        binding.edittextInquiryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onTitleTextChanged(s.toString()); // 뷰 모델에 알림
                binding.textviewInquiryTopConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onTitleAfterTextChanged(s.toString()); // 뷰 모델에 알림
                if (s.length() > 32) {
                    binding.edittextInquiryTitle.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextInquiryTitle.setText(text);
                    binding.edittextInquiryTitle.setSelection(text.length());
                    binding.edittextInquiryTitle.addTextChangedListener(this);
                }

                if (s.length() <= 32) {
                    binding.textviewFolderNameCount.setText(s.length() + "/32");
                    binding.textviewInquiryTopConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }

                if (s.length() == 0) {
                    binding.textviewInquiryTopConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

        binding.edittextInquiryDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onDescriptionTextChanged(s.toString()); // 뷰 모델에 알림
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}