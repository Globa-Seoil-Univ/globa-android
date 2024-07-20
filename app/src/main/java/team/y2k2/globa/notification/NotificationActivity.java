package team.y2k2.globa.notification;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.NotificationInquiryResponse;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.databinding.ActivityNotificationBinding;
import team.y2k2.globa.notification.inquiry.InquiryAdapter;
import team.y2k2.globa.notification.inquiry.InquiryItem;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationBinding binding;

    NotificationModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

        binding.linearlayoutNotificationTypeAll.setOnClickListener(this);
        binding.linearlayoutNotificationTypeDocs.setOnClickListener(this);
        binding.linearlayoutNotificationInquiry.setOnClickListener(this);
        binding.linearlayoutNotificationTypeAnnouncement.setOnClickListener(this);
        binding.linearlayoutNotificationTypeShare.setOnClickListener(this);

        binding.imagebuttonNotificationBack.setOnClickListener(v -> {
            finish();
        });

        setNotificationDefaultType(binding.linearlayoutNotificationTypeAll);

        loadNotificationAdapter();
        setContentView(binding.getRoot());
    }

    @Override
    public void onClick(View textView) {

        if(textView.getId() == binding.linearlayoutNotificationTypeAll.getId()) {
            setNotificationDefaultType(binding.linearlayoutNotificationTypeAll);
            loadNotificationAdapter();
        }
        else if(textView.getId() == binding.linearlayoutNotificationTypeDocs.getId()) {
            setNotificationDefaultType(binding.linearlayoutNotificationTypeDocs);
        }
        else if(textView.getId() == binding.linearlayoutNotificationInquiry.getId()) {
            setNotificationDefaultType(binding.linearlayoutNotificationInquiry);
            loadInquiryAdapter();
        }
        else if(textView.getId() == binding.linearlayoutNotificationTypeAnnouncement.getId()) {
            setNotificationDefaultType(binding.linearlayoutNotificationTypeAnnouncement);
        }
        else if(textView.getId() == binding.linearlayoutNotificationTypeShare.getId()) {
            setNotificationDefaultType(binding.linearlayoutNotificationTypeShare);
        }
    }

    public void setNotificationDefaultType(LinearLayout linearLayout) {
        binding.textviewNotificationTypeAll.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.textviewNotificationTypeDocs.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.textviewNotificationTypeAnnouncement.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.textviewNotificationTypeShare.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.textviewNotificationTypeInquiry.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.linearlayoutNotificationTypeAllBar.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationTypeDocsBar.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationTypeAnnouncementBar.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationTypeShareBar.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationTypeInquiryBar.setVisibility(View.INVISIBLE);

        if(linearLayout.getId() == binding.linearlayoutNotificationTypeAll.getId()) {
            binding.linearlayoutNotificationTypeAllBar.setVisibility(View.VISIBLE);
            binding.textviewNotificationTypeAll.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
        else if(linearLayout.getId() == binding.linearlayoutNotificationTypeDocs.getId()) {
            binding.linearlayoutNotificationTypeDocsBar.setVisibility(View.VISIBLE);
            binding.textviewNotificationTypeDocs.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
        else if(linearLayout.getId() == binding.linearlayoutNotificationInquiry.getId()) {
            binding.linearlayoutNotificationTypeInquiryBar.setVisibility(View.VISIBLE);
            binding.textviewNotificationTypeInquiry.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
        else if(linearLayout.getId() == binding.linearlayoutNotificationTypeAnnouncement.getId()) {
            binding.linearlayoutNotificationTypeAnnouncementBar.setVisibility(View.VISIBLE);
            binding.textviewNotificationTypeAnnouncement.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
        else if(linearLayout.getId() == binding.linearlayoutNotificationTypeShare.getId()) {
            binding.linearlayoutNotificationTypeShareBar.setVisibility(View.VISIBLE);
            binding.textviewNotificationTypeShare.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
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
                    ArrayList<InquiryItem> items = new ArrayList<>();

                    for(int i = 0; i < inquiryResponse.getInquires().size(); i++) {
                        String title = inquiryResponse.getInquires().get(i).getTitle();
                        String content = inquiryResponse.getInquires().get(i).getContent();
                        String inquiryId = inquiryResponse.getInquires().get(i).getInquiryId();
                        String createdTime = inquiryResponse.getInquires().get(i).getCreatedTime();
                        boolean isSolved = inquiryResponse.getInquires().get(i).isSolved();

                        items.add(new InquiryItem(inquiryId, title, content, createdTime, isSolved));

                        Log.d("Notification Inquiry", "title : " + title);
                        Log.d("Notification Inquiry", "content : " + content);
                        Log.d("Notification Inquiry", "isSolved : " + isSolved);

                    }

                    InquiryAdapter inquiryAdapter = new InquiryAdapter(items);

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

    public void loadNotificationAdapter() {
        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SharedPreferences preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        ApiService apiService = retrofit.create(ApiService.class);


//        Call<InquiryResponse> call = apiService.requestGetInquires("application/json",accessToken, 1, 10, "r");
//        call.enqueue(new Callback<InquiryResponse>() {
//            @Override
//            public void onResponse(Call<InquiryResponse> call, Response<InquiryResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // 성공적으로 응답을 받았을 때 처리할 내용
//                    Log.d("Notification Inquiry", "성공 : " + response.code());
//
//                    InquiryResponse inquiryResponse = response.body();
//                    ArrayList<InquiryItem> items = new ArrayList<>();
//
//                    for(int i = 0; i < inquiryResponse.getInquires().size(); i++) {
//                        String title = inquiryResponse.getInquires().get(i).getTitle();
//                        String content = inquiryResponse.getInquires().get(i).getContent();
//                        boolean isSolved = inquiryResponse.getInquires().get(i).isSolved();
//
//                        items.add(new InquiryItem(title, content, isSolved));
//
//                        Log.d("Notification Inquiry", "title : " + title);
//                        Log.d("Notification Inquiry", "content : " + content);
//                        Log.d("Notification Inquiry", "isSolved : " + isSolved);
//
//                    }
//
//                    InquiryAdapter inquiryAdapter = new InquiryAdapter(items);
//
//                    binding.recyclerviewNotification.setAdapter(inquiryAdapter);
//                    binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
//                } else {
//                    // 서버로부터 실패 응답을 받았을 때 처리할 내용
//                    Log.d("Notification Inquiry", "실패 : " + response.code() + " | " + response);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<InquiryResponse> call, Throwable t) {
//                // 네트워크 요청 실패 시 처리할 내용
//                Log.d("Notification Inquiry", "실패 : " + t.getMessage());
//            }
//        });



        Call<NotificationResponse> call2 = apiService.requestGetAllNotification("application/json",accessToken);
        call2.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때 처리할 내용
                    Log.d("Notification Inquiry", "성공 : " + response.code());

                    NotificationResponse notificationResponse = response.body();
                    model = new NotificationModel(notificationResponse);


                    NotificationAdapter inquiryAdapter = new NotificationAdapter(model);

                    binding.recyclerviewNotification.setAdapter(inquiryAdapter);
                    binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리할 내용
                    Log.d("Notification", "실패 : " + response.code() + " | " + response);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리할 내용
                Log.d("Notification", "실패 : " + t.getMessage());
            }
        });
    }
}
