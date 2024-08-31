package team.y2k2.globa.main.profile.edit;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.NicknameEditRequest;

public class NicknameEditViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public NicknameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void updateNickname(Context context, String userId, String newNickname) {
        NicknameEditRequest request = new NicknameEditRequest(newNickname);

        try {
            apiService.requestUpdateProfileName(userId, "application/json", authorization, request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Log.d(getClass().getName(), "닉네임 업데이트 성공");
                        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", newNickname);
                        editor.commit();
                    } else {
                        Log.d(getClass().getName(), "닉네임 업데이트 실패 : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    errorLiveData.setValue("서버 오류 발생");
                    Log.d(getClass().getName(), "닉네임 업데이트 실패"  + t.getMessage());
                }
            });
        } catch(Exception e) {
            Log.d(getClass().getName(), "서버 오류 발생: " + e.getMessage());
        }
    }
}
