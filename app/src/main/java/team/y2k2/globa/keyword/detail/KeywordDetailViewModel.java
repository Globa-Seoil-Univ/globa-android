package team.y2k2.globa.keyword.detail;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;

public class KeywordDetailViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<KeywordDetailResponse> keywordDetailResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public KeywordDetailViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<KeywordDetailResponse> getKeywordDetailResponseLiveData() {
        return keywordDetailResponseLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void searchKeyword(String keyword) {
        apiService.searchKeyword("application/json", authorization, keyword).enqueue(new Callback<KeywordDetailResponse>() {
            @Override
            public void onResponse(Call<KeywordDetailResponse> call, Response<KeywordDetailResponse> response) {
                if(response.isSuccessful()) {
                    keywordDetailResponseLiveData.setValue(response.body());
                    Log.d("api 통신 성공", "api 수신 성공" + response.code());
                } else {
                    Log.d("api 통신 실패", "api 수신 실패" + response.code());
                }
            }

            @Override
            public void onFailure(Call<KeywordDetailResponse> call, Throwable t) {
                errorLiveData.setValue("서버 오류 발생" + t.getMessage());
                Log.d("api 통신 실패", "api 송신 실패" + t.getMessage());
            }
        });
    }


}
