package team.y2k2.globa.main.profile.inquiry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.InquiryApiClient;
import team.y2k2.globa.databinding.ActivityInquiryBinding;

public class InquiryActivity extends AppCompatActivity {

    ActivityInquiryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryBinding.inflate(getLayoutInflater());

        binding.imageviewInquiryTopBack.setOnClickListener(v -> finish());

        binding.textviewInquiryTopConfirm.setOnClickListener(v -> {
            String title = binding.edittextInquiryTitle.getText().toString();
            String content = binding.edittextInquiryDescription.getText().toString();

            InquiryApiClient apiClient = new InquiryApiClient();
            Response<Void> response = apiClient.requestInsertInquiry(title, content);

            if (response.isSuccessful()) {
                Toast.makeText(binding.getRoot().getContext(), "문의를 보냈습니다.", Toast.LENGTH_LONG).show();
                Log.d("INQUIRY_RESULT", "문의 추가 성공");
                finish();
            } else {
                Toast.makeText(binding.getRoot().getContext(), response.code(), Toast.LENGTH_LONG).show();
                Log.d("INQUIRY_RESULT", "문의 추가 실패" + response.code());

            }
        });

        binding.edittextInquiryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textviewInquiryTopConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        setContentView(binding.getRoot());
    }
}
