package team.y2k2.globa.main.profile.info;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public void uploadImage(byte[] imageData, String userId, String authorization) {
        // ByteArray를 RequestBody로 변환
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageData);

        // MultipartBody.Part로 변환
        MultipartBody.Part profilePart = MultipartBody.Part.createFormData("profile", "image.jpg", requestFile);

        apiService.requestUpdateProfileImage(userId, "multipart/form-data", authorization, profilePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "이미지 업로드 완료");
                } else {
                    Log.e(getClass().getName(), "이미지 업로드 실패");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(getClass().getName(), "서버 통신 실패");
            }
        });
    }

}
