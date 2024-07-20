package team.y2k2.globa.main.profile.inquiry;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityInquiryBinding;

public class InquiryActivity extends AppCompatActivity {

    ActivityInquiryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInquiryBinding.inflate(getLayoutInflater());


        binding.textviewInquiryTopConfirm.setOnClickListener(v -> {
            String title = binding.edittextInquiryTitle.getText().toString();
            String content = binding.edittextInquiryDescription.getText().toString();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            SharedPreferences preferences = binding.getRoot().getContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
            String accessToken = "Bearer " + preferences.getString("accessToken", "");

            InquiryRequest inquiryRequest = new InquiryRequest(title, content);

            Call<Void> call = apiService.requestInsertInquiry("application/json",accessToken, inquiryRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(binding.getRoot().getContext(), "문의를 보냈습니다.", Toast.LENGTH_LONG);
                        // 성공적으로 응답을 받았을 때 처리
                        Log.d("INQUIRY_RESULT", "문의 추가 성공");
                        finish();
                    } else {
                        // 서버로부터 실패 응답을 받았을 때 처리
                        Toast.makeText(binding.getRoot().getContext(), response.code(), Toast.LENGTH_LONG);
                        Log.d("INQUIRY_RESULT", "문의 추가 실패" + response.code());

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // 네트워크 요청 실패 시 처리
                    Toast.makeText(binding.getRoot().getContext(), t.getMessage(), Toast.LENGTH_LONG);
                    Log.d("INQUIRY_RESULT", "네트워크 에러" + t.getMessage());

                }
            });
        });

        setContentView(binding.getRoot());
    }
}
