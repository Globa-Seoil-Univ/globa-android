package team.y2k2.globa.main.profile.edit;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        apiService.requestUpdateProfileName(userId, "application/json", authorization, newNickname).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "이름 수정 성공");
                } else {
                    Log.d(getClass().getName(), "이름 수정 실패");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }


}
