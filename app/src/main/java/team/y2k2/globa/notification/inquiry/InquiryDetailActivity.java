package team.y2k2.globa.notification.inquiry;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.databinding.ActivityInquiryDetailBinding;

public class InquiryDetailActivity extends AppCompatActivity {

    ActivityInquiryDetailBinding binding;
    InquiryDetailViewModel viewModel;

    String inquiryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(InquiryDetailViewModel.class);

        Intent intent = getIntent();
        inquiryId = intent.getStringExtra("inquiryId");

        viewModel.fetchInquiryDetail(inquiryId);

        viewModel.getInquiryDetail().observe(this, inquiryDetailResponse -> {
            if (inquiryDetailResponse != null) {
                binding.textviewItemInquiryTitle.setText(inquiryDetailResponse.getTitle());
                binding.textviewItemInquiryDescription.setText(inquiryDetailResponse.getContent());
                binding.textviewItemInquiryDatetime.setText(inquiryDetailResponse.getCreatedTime());
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                binding.textviewItemInquiryTitle.setText(errorMessage);
                binding.textviewItemInquiryDescription.setText("잘못된 접근입니다.");
                binding.textviewItemInquiryDatetime.setText("");
            }
        });

        binding.imagebuttonInquiryDetailBack.setOnClickListener(v -> finish());
    }
}