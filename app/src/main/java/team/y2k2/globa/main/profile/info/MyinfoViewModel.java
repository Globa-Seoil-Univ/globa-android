package team.y2k2.globa.main.profile.info;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class MyinfoViewModel extends ViewModel {

    private ApiService apiService; // Retrofit2
    private MutableLiveData<UserInfoResponse> userInfoResponseLiveData = new MutableLiveData<UserInfoResponse>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MyinfoViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void uploadImage(MultipartBody.Part profilePart, String userId) {

        apiService.requestUpdateProfileImage(userId, "multipart/form-data", authorization, profilePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getSimpleName(), "이미지 업로드 완료: " + response.code());
                } else {
                    Log.d(getClass().getSimpleName(), "이미지 업로드 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "이미지 업로드 onFailure(): " + t.getMessage());
            }
        });
    }

}
