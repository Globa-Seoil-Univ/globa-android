package team.y2k2.globa.notification;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCountResponse;

public class NotificationViewModel extends ViewModel {

    private NotificationActivity activity;
    private ApiService apiService;
    private MutableLiveData<NotificationResponse> notificationLiveData = new MutableLiveData<>();
    private MutableLiveData<UnreadNotificationCountResponse> unreadCount = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public NotificationViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<NotificationResponse> getNotificationLiveData() {
        return notificationLiveData;
    }
    public MutableLiveData<UnreadNotificationCountResponse> getUnreadCount() {
        return unreadCount;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getNotification(String type) {
        Log.d("알림 API", "알림 API 요청 타입: " + type);
        apiService.requestGetNotification("application/json", authorization, 1, 10, type).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if(response.isSuccessful()) {
                    // Gson 객체 생성
                    // response.body()를 JSON 문자열로 변환
                    // 변환된 JSON 문자열 출력

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());

                    Log.d(getClass().getSimpleName(), "Raw Response: " + jsonResponse);

                    notificationLiveData.postValue(response.body());
                    Log.d("알림", "알림 수신 성공 : " + response.code());
                } else {
                    notificationLiveData.postValue(response.body());
                    Log.d("알림", "알림 수신 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                errorLiveData.postValue(t.getMessage());
                Log.d("알림", "알림 송신 오류 : " + t.getMessage());
            }
        });
    }

    public void acceptInvite(String folderId, String shareId) {
        apiService.requestAcceptShareInvite(folderId, shareId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("공유 초대", "공유 초대 수락 완료");
                } else {
                    Toast.makeText(activity, "공유 초대 수락 실패", Toast.LENGTH_SHORT).show();
                    Log.d("공유 초대", "공유 초대 수락 실패 : " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("공유 초대", "공유 초대 수락 요청 오류 : " + t.getMessage());
            }
        });
    }

    public void denyInvite(String folderId, String shareId, String notificationId) {
        NotificationRequest notificationRequest = new NotificationRequest(notificationId);
        apiService.requestDeniedShareInvite(folderId, shareId, "application/json", authorization, notificationRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("공유 초대", "공유 초대 거절 완료");
                } else {
                    Toast.makeText(activity, "공유 초대 거절 실패", Toast.LENGTH_SHORT).show();
                    Log.d("공유 초대", "공유 초대 거절 실패 : " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("공유 초대", "공유 초대 거절 요청 오류 : " + t.getMessage());
            }
        });
    }

    public void getUnreadNotificationCount() {
        apiService.getUnreadNotificationCount("application/json", authorization).enqueue(new Callback<UnreadNotificationCountResponse>() {
            @Override
            public void onResponse(Call<UnreadNotificationCountResponse> call, Response<UnreadNotificationCountResponse> response) {
                if(response.isSuccessful()) {
                    unreadCount.postValue(response.body());
                    Log.d("안 읽은 알림 개수", "안 읽은 알림 개수 가져오기 성공");
                } else {
                    Log.d("안 읽은 알림 개수", "안 읽은 알림 개수 가져오기 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UnreadNotificationCountResponse> call, Throwable t) {
                Log.d("안 읽은 알림 개수", "안 읽은 알림 개수 가져오기 요청 실패");
            }
        });
    }

    public void readNotification(String notificationId) {
        apiService.readNotification(notificationId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("알림 읽기", "알림 읽기 요청 완료");
                } else {
                    Log.d("알림 읽기", "알림 읽기 요청 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("알림 읽기", "알림 읽기 요청 실패 : " + t.getMessage());
            }
        });
    }
}
