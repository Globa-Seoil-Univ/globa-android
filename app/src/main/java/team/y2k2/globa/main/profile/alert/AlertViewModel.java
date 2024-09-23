package team.y2k2.globa.main.profile.alert;

import static team.y2k2.globa.api.ApiClient.authorization;
import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.AlertRequest;
import team.y2k2.globa.api.model.response.AlertResponse;

public class AlertViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<AlertResponse> alertLiveData = new MutableLiveData<>();

    public AlertViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<AlertResponse> getAlertLiveData() {
        return alertLiveData;
    }

    public void getMyAlertStatus(String userId) {
        apiService.getMyAlertStatus(userId, APPLICATION_JSON, authorization).enqueue(new Callback<AlertResponse>() {
            @Override
            public void onResponse(Call<AlertResponse> call, Response<AlertResponse> response) {
                if(response.isSuccessful()) {
                    alertLiveData.setValue(response.body());
                    Log.d(getClass().getSimpleName(), "내 알림 정보 요청 결과: " + response.code());
                } else {
                    Log.d(getClass().getSimpleName(), "내 알림 정보 요청 결과: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AlertResponse> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "내 알림 정보 요청 실패: " + t.getMessage());
            }
        });
    }

    public void requestAlertStatus(String userId, boolean uploadNofi, boolean shareNofi, boolean eventNofi) {
        AlertRequest alertRequest = new AlertRequest(uploadNofi, shareNofi, eventNofi);
        apiService.requestAlertStatus(userId, APPLICATION_JSON, authorization, alertRequest).enqueue(new Callback<AlertResponse>() {
            @Override
            public void onResponse(Call<AlertResponse> call, Response<AlertResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getSimpleName(), "알림 수정 요청 완료: " + response.code());
                } else {
                    Log.d(getClass().getSimpleName(), "알림 수정 요청 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AlertResponse> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "알람 수정 요청 실패: " + t.getMessage());
            }
        });
    }

}
