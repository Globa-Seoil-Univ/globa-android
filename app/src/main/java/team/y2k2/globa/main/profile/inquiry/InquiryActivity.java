 package team.y2k2.globa.main.profile.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        viewModel = new ViewModelProvider(this).get(InquiryViewModel.class);

        binding.setViewModel(viewModel);
        setupRecyclerView();
        observeViewModel();

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.layoutInquiryList.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.layoutInquiryList.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            binding.layoutInquiryList.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });

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

        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish != null && shouldFinish) {
                finish();
            }
        });
    }
}