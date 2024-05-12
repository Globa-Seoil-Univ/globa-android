package team.y2k2.globa.main.profile.edit;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.security.auth.callback.Callback;

import team.y2k2.globa.api.ApiService;

public class NicknameEditViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public NicknameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void updateNickname(String userId, String authorization, String newNickname) {
        try {
            apiService.requestUpdateProfileName(userId, "application/json", authorization, newNickname);
            Log.d(getClass().getName(), "닉네임 업데이트 성공");
        } catch(Exception e) {
            errorLiveData.setValue("서버 오류 발생");
            Log.d(getClass().getName(), "서버 오류 발생: " + e.getMessage());
        }

    }


}
