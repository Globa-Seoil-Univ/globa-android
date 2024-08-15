package team.y2k2.globa.docs.statistics;

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

public class DocsStatisticsViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<StatisticsResponse> docsStatisticsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public DocsStatisticsViewModel() {
        apiService = ApiClient.getApiService();
    }
    public LiveData<StatisticsResponse> getDocsStatisticsLiveData() {
        return docsStatisticsLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getDocsStatistics(String folderId, String recordId) {
        apiService.requestDocStatistics(folderId, recordId, "application/json", authorization).enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if(response.isSuccessful()) {
                    docsStatisticsLiveData.postValue(response.body());
                    Log.d(getClass().getName(), "서버 응답 성공!");
                } else {
                    Log.e(getClass().getName(), "서버 응답 오류 발생 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Log.e(getClass().getName(), "서버 통신 실패: " + t.getMessage());
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

}
