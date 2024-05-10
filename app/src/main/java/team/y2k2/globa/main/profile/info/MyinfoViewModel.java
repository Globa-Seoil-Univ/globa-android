package team.y2k2.globa.main.profile.info;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.main.profile.UserInfoResponse;

public class MyinfoViewModel extends ViewModel {

    private ApiService apiService; // Retrofit2
    private MutableLiveData<UserInfoResponse> userInfoResponseLiveData = new MutableLiveData<UserInfoResponse>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MyinfoViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<UserInfoResponse> getUserInfoResponseLiveData() {
        return userInfoResponseLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchMyInfo(String contentType, String authorization) {
        apiService.requestUserInfo(contentType, authorization).enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if(response.isSuccessful()) {
                    userInfoResponseLiveData.setValue(response.body());
                    Log.d(getClass().getName(), "유저 정보 조회 성공" + response.body());
                } else {
                    errorLiveData.setValue("서버 오류 발생");
                    Log.d(getClass().getName(), response.code() + " 오류 :" + response.message());
                }
            }

            @Override
            public void onFailure(Call<team.y2k2.globa.main.profile.UserInfoResponse> call, Throwable t) {
                errorLiveData.setValue("네트워크 오류 발생");

                Log.d(getClass().getName(), "네트워크 오류 발생:" + t);

            }
        });
    }

}
