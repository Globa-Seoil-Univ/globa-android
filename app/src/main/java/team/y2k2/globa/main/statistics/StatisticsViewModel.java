package team.y2k2.globa.main.statistics;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class StatisticsViewModel extends ViewModel {

    private UserApiClient apiClient;
    private final MutableLiveData<StatisticsResponse> statisticsLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserInfoResponse> userInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new UserApiClient(context);
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

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void getUserId() {
        UserInfoResponse response = apiClient.requestUserInfo();
        userInfoLiveData.postValue(response);
    }

    public void getStatistics(String userId) {
        isLoading.setValue(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                StatisticsResponse response = apiClient.requestStatistics();
                statisticsLiveData.postValue(response);
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void loadAllStatistics() {
        isLoading.setValue(true); // 로딩 시작

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // 1. 백그라운드에서 유저 정보 먼저 조회
                UserInfoResponse userInfo = apiClient.requestUserInfo();
                if (userInfo != null && userInfo.getUserId() != null) {
                    // 2. 유저 ID로 통계 정보 조회
                    StatisticsResponse statsResponse = apiClient.requestStatistics();
                    statisticsLiveData.postValue(statsResponse);
                } else {
                    // 유저 정보 조회 실패 시 에러 처리
                    errorLiveData.postValue("사용자 정보를 가져올 수 없습니다.");
                }
            } catch (Exception e) {
                // 예외 발생 시 에러 처리
                errorLiveData.postValue("데이터 로딩 중 오류가 발생했습니다.");
            } finally {
                // API 요청이 성공하든 실패하든 항상 로딩 상태를 종료
                isLoading.postValue(false);
            }
        });
    }
}