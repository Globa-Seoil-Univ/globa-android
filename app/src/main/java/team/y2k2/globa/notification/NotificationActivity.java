package team.y2k2.globa.notification;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityNotificationBinding;
import team.y2k2.globa.notification.inquiry.NotificationInquiryAdapter;
import team.y2k2.globa.notification.inquiry.NotificationInquiryItem;
import team.y2k2.globa.notification.inquiry.NotificationInquiryResponse;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

        loadInquiryAdapter();
        setContentView(binding.getRoot());
    }


    public void loadInquiryAdapter() {
        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SharedPreferences preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        ApiService apiService = retrofit.create(ApiService.class);

        // 네트워크 요청 보내기
//        Log.d("Record UPLOAD", "업로드: " + title + " | " + path);

        Call<NotificationInquiryResponse> call = apiService.requestGetInquires("application/json",accessToken, 1, 10, "r");
        call.enqueue(new Callback<NotificationInquiryResponse>() {
            @Override
            public void onResponse(Call<NotificationInquiryResponse> call, Response<NotificationInquiryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때 처리할 내용
                    Log.d("Notification Inquiry", "성공 : " + response.code());

                    NotificationInquiryResponse inquiryResponse = response.body();
                    ArrayList<NotificationInquiryItem> items = new ArrayList<>();

                    for(int i = 0; i < inquiryResponse.getInquires().size(); i++) {
                        String title = inquiryResponse.getInquires().get(i).getTitle();
                        String content = inquiryResponse.getInquires().get(i).getContent();
                        boolean isSolved = inquiryResponse.getInquires().get(i).isSolved();

                        items.add(new NotificationInquiryItem(title, content, isSolved));

                        Log.d("Notification Inquiry", "title : " + title);
                        Log.d("Notification Inquiry", "content : " + content);
                        Log.d("Notification Inquiry", "isSolved : " + isSolved);

                    }

                    NotificationInquiryAdapter inquiryAdapter = new NotificationInquiryAdapter(items);

                    binding.recyclerviewNotification.setAdapter(inquiryAdapter);
                    binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리할 내용
                    Log.d("Notification Inquiry", "실패 : " + response.code() + " | " + response);
                }
            }

            @Override
            public void onFailure(Call<NotificationInquiryResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리할 내용
                Log.d("Notification Inquiry", "실패 : " + t.getMessage());
            }
        });


    }


}
