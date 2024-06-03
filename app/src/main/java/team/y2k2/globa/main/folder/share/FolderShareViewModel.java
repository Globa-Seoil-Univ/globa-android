package team.y2k2.globa.main.folder.share;

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
import team.y2k2.globa.api.model.request.FolderShareAddRequest;
import team.y2k2.globa.api.model.response.UserSearchResponse;

public class FolderShareViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<UserSearchResponse> userSearchLiveData = new MutableLiveData<UserSearchResponse>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderShareViewModel() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<UserSearchResponse> getUserSearchLiveData() {
        return userSearchLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void searchUserInfo(String userCode) {
        apiService.requestSearchUserInfo("application/json", authorization, userCode).enqueue(new Callback<UserSearchResponse>() {
            @Override
            public void onResponse(Call<UserSearchResponse> call, Response<UserSearchResponse> response) {
                if(response.isSuccessful()) {
                    userSearchLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("해당 코드의 사용자가 없습니다");
                }
            }

            @Override
            public void onFailure(Call<UserSearchResponse> call, Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void addSharedUser(int folderId, int userId, String role) {
        FolderShareAddRequest folderShareAddRequest = new FolderShareAddRequest(role);
        apiService.requestInsertFolderShareUser(folderId, userId, "application/json", authorization, folderShareAddRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "공유 추가 성공");
                } else {
                    Log.d(getClass().getName(), "공유 추가 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
            }
        });
    }

}
