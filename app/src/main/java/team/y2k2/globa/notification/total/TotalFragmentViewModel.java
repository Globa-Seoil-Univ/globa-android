package team.y2k2.globa.notification.total;

import static team.y2k2.globa.api.ApiClient.authorization;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.NotificationResponse;

public class TotalFragmentViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<NotificationResponse> totalNotificationLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public TotalFragmentViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<NotificationResponse> getTotalNotificationLiveData() {
        return totalNotificationLiveData;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getAllNotification(String type) {
        apiService.requestGetAllNotification("application/json", authorization, 1, 10, type).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if(response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {

            }
        });
    }

}
