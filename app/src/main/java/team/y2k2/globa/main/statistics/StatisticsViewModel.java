package team.y2k2.globa.main.statistics;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.User;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class StatisticsViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<StatisticsResponse> statisticsLiveData = new MutableLiveData<>();
    private MutableLiveData<UserInfoResponse> userInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public StatisticsViewModel() {
        apiService = ApiClient.getApiService();
    }
    public LiveData<StatisticsResponse> getStatisticsLiveData() {
        return statisticsLiveData;
    }
    public LiveData<UserInfoResponse> getUserInfoLiveData() {
        return userInfoLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getUserId() {
        apiService.requestUserInfo("application/json", authorization).enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if(response.isSuccessful()) {
                    userInfoLiveData.postValue(response.body());
                    Log.d("api 수신 성공", "api 수신 성공");
                } else {
                    Log.d("api 수신 실패", "api 수신 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.d("api 송신 실패", "api 송신 실패 : " + t.getMessage());
            }
        });
    }

    public void getStatistics(String userId) {
        apiService.requestStatistics(userId, "application/json", authorization).enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if(response.isSuccessful()) {
                    statisticsLiveData.postValue(response.body());
                    Log.d(getClass().getSimpleName(), "시각화 자료 응답 코드: " + response.code());
                } else {
                    Log.d(getClass().getSimpleName(), "시각화 자료 응답 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
                Log.d(getClass().getSimpleName(), "시각화 자료 요청 onFailure() : " + t.getMessage());
            }
        });
    }

}
