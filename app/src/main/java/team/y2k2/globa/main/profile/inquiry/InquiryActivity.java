 package team.y2k2.globa.main.profile.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import team.y2k2.globa.databinding.ActivityInquiryListBinding;
import team.y2k2.globa.main.profile.inquiry.add.InquiryAddActivity;

public class InquiryActivity extends AppCompatActivity {

    private ActivityInquiryListBinding binding;
    private InquiryViewModel viewModel;
    private InquiryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(InquiryViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.fabAddInquiry.setOnClickListener(v -> {
            Intent intent = new Intent(InquiryActivity.this, InquiryAddActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new InquiryAdapter(new ArrayList<>());
        binding.recyclerviewInquiries.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewInquiries.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadInquiries();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressbarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsEmpty().observe(this, isEmpty -> {
            binding.textviewEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerviewInquiries.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        viewModel.getInquiries().observe(this, inquiryItems -> {
            if (inquiryItems != null) {
                adapter.updateData(inquiryItems);
            }
        });
    }
}