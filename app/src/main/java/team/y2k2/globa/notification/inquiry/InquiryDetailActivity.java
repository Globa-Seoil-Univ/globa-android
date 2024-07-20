package team.y2k2.globa.notification.inquiry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;
import team.y2k2.globa.databinding.ActivityInquiryDetailBinding;

public class InquiryDetailActivity extends AppCompatActivity {

    ActivityInquiryDetailBinding binding;

    String inquiryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryDetailBinding.inflate(this.getLayoutInflater());

        Intent intent = getIntent();
        inquiryId = intent.getStringExtra("inquiryId");

        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        binding.imagebuttonInquiryDetailBack.setOnClickListener(v -> {
            finish();
        });

        SharedPreferences preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        ApiService apiService = retrofit.create(ApiService.class);

        Call<InquiryDetailResponse> call2 = apiService.requestGetInquiryDetail(inquiryId,"application/json",accessToken);
        call2.enqueue(new Callback<InquiryDetailResponse>() {
            @Override
            public void onResponse(Call<InquiryDetailResponse> call, Response<InquiryDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때 처리할 내용
                    InquiryDetailResponse inquiryDetailResponse = response.body();

                    binding.textviewItemInquiryTitle.setText(inquiryDetailResponse.getTitle());
                    binding.textviewItemInquiryDescription.setText(inquiryDetailResponse.getContent());
                    binding.textviewItemInquiryDatetime.setText(inquiryDetailResponse.getCreatedTime());

                    Log.d("Notification Inquiry", "성공 : " + response.code());
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리할 내용
                    Log.d("Notification", "실패 : " + response.code() + " | " + response);
                }
            }
            @Override
            public void onFailure(Call<InquiryDetailResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리할 내용
                Log.d("Notification", "실패 : " + t.getMessage());
            }
        });

        setContentView(binding.getRoot());
    }
}
