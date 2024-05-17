package team.y2k2.globa.main.folder.share;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class FolderShareViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<UserInfoResponse> userInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderShareViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<UserInfoResponse> getUserInfoLiveData() {
        return userInfoLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchUserInfo(String authorization, String userCode) {
        apiService.requestSearchUserInfo("application/json", authorization, userCode).enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if(response.isSuccessful()) {
                    userInfoLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Failed to fetch user info");
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

}
