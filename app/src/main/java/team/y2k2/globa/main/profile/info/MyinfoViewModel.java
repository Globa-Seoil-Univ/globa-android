package team.y2k2.globa.main.profile.info;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import okhttp3.MultipartBody;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;

public class MyinfoViewModel extends ViewModel {

    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private ApiClient apiClient; // Retrofit2

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void uploadImage(MultipartBody.Part multipartBody, String userId) {
        Response<Void> response = apiClient.requestUpdateProfileImage(multipartBody, userId);

        if (response.isSuccessful()) {
            Log.d(getClass().getSimpleName(), "이미지 업로드 완료: " + response.code());
        } else {
            Log.d(getClass().getSimpleName(), "이미지 업로드 실패: " + response.code() + ", " + response.message());
        }
    }
}
