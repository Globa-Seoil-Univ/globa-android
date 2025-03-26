package team.y2k2.globa.main.profile.edit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.NicknameEditRequest;

public class NicknameEditViewModel extends ViewModel {

    private UserApiClient apiClient;
    private final MutableLiveData<String> nameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new UserApiClient();
    }

    public MutableLiveData<String> getNameLiveData() {
        return nameLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void updateNickname(Context context, String userId, String newNickname) {
        NicknameEditRequest request = new NicknameEditRequest(newNickname);

        Response<Void> response = apiClient.requestUpdateProfileName(userId, newNickname);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "닉네임 업데이트 성공");
            SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", newNickname);
            editor.apply();
            nameLiveData.setValue(newNickname);
        } else {
            Log.d(getClass().getName(), "닉네임 업데이트 실패 : " + response.code());
        }
    }
}
