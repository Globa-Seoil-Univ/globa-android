package team.y2k2.globa.notification;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.response.NotificationResponse;

public class NotificationViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<NotificationResponse> NotificationLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public NotificationViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<NotificationResponse> getNotificationLiveData() {
        return NotificationLiveData;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getNotification(String type) {
        apiService.requestGetNotification("application/json", authorization, 1, 10, type).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if(response.isSuccessful()) {
                    NotificationLiveData.postValue(response.body());
                    Log.d("알림", "알림 수신 성공 : " + response.code());
                } else {
                    Log.d("알림", "알림 수신 실패 : " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                errorLiveData.postValue("알림 송신 오류 : " + t.getMessage());
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
                    Log.d("공유 초대", "공유 초대 거절 실패 : " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("공유 초대", "공유 초대 거절 요청 오류 : " + t.getMessage());
            }
        });
    }

}
