package team.y2k2.globa.main.statistics;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class StatisticsViewModel extends ViewModel {

    private UserApiClient apiClient;
    private final MutableLiveData<StatisticsResponse> statisticsLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserInfoResponse> userInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new UserApiClient();
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
        UserInfoResponse response = apiClient.requestUserInfo();
        userInfoLiveData.postValue(response);
    }

    public void getStatistics(String userId) {
        StatisticsResponse response = apiClient.requestStatistics(userId);
        statisticsLiveData.postValue(response);
    }
}
