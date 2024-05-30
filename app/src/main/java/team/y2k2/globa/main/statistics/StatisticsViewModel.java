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
import team.y2k2.globa.api.model.response.StatisticsResponse;

public class StatisticsViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<StatisticsResponse> statisticsLiveData = new MutableLiveData<StatisticsResponse>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public StatisticsViewModel() {
        apiService = ApiClient.getApiService();
    }
    public LiveData<StatisticsResponse> getStatisticsLiveData() {
        return statisticsLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getStatistics(int userId) {
        apiService.requestStatistics(userId, "application/json", authorization).enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if(response.isSuccessful()) {
                    statisticsLiveData.postValue(response.body());
                } else {
                    Log.e(getClass().getName(), "서버 응답 오류 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
            }
        });
    }

}
