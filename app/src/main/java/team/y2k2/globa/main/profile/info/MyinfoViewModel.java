package team.y2k2.globa.main.profile.info;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.UserInfoResponse;

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

    public void uploadImage(File imageFile, String userId) {
        // ByteArray를 RequestBody로 변환
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

        // MultipartBody.Part로 변환
        MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", imageFile.getName(), requestFile);

        apiService.requestUpdateProfileImage(userId, "multipart/form-data", authorization, profilePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "이미지 업로드 완료");
                } else {
                    Log.d(getClass().getName(), "이미지 업로드 실패: " + response.code() + ", " + response.message());
                    Log.d(getClass().getName(), "파일: " + profilePart);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getName(), "서버 통신 실패: " + t.getMessage());
            }
        });
    }

}
