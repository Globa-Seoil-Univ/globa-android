package team.y2k2.globa.main.folder.inside;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;

public class FolderInsideViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<Integer> responseCodeLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderInsideViewModel() {
        apiService = ApiClient.getApiService();
    }
    public MutableLiveData<Integer> getResponseCodeLiveData() {
        return responseCodeLiveData;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void deleteFolder(int folderId) {
        apiService.requestDeleteFolder(folderId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("API 수신 성공", "폴더 삭제 성공 : " + response.code());
                    responseCodeLiveData.setValue(response.code());
                } else {
                    Log.d("API 수신 오류", "폴더 삭제 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("API 송신 오류", "오류 메시지 : " + t.getMessage());
            }
        });
    }

}
