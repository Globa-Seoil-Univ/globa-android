package team.y2k2.globa.main.folder.permission;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.FolderPermissionChangeRequest;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;

public class FolderPermissionViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<FolderPermissionResponse> usersLiveData;
    private MutableLiveData<String> errorLiveData;

    public FolderPermissionViewModel() {
        apiService = ApiClient.getApiService();
        usersLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<FolderPermissionResponse> getUsersLiveData() {
        return usersLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchSharedUsers(int folderId, int page, int count) {
        apiService.requestFoloderShareUser(folderId, "application/json", authorization, page, count).enqueue(new Callback<FolderPermissionResponse>() {
            @Override
            public void onResponse(Call<FolderPermissionResponse> call, Response<FolderPermissionResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    usersLiveData.setValue(response.body());
                    Log.d("API 수신 성공", "응답 코드 : " + response.code());
                } else {
                    Log.d("API 수신 오류", "오류 코드 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FolderPermissionResponse> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
                Log.d("API 송신 오류", "오류 메시지 : " + t.getMessage());
            }
        });
    }

    public void changeSharedUsers(int folderId, int userId, String userRole) {
        FolderPermissionChangeRequest folderPermissionChangeRequest = new FolderPermissionChangeRequest(userRole);
        apiService.requestUpdateSharePermission(folderId, userId, "application/json", authorization, folderPermissionChangeRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("API 수신 완료", "응답 코드 : " + response.code());
                } else {
                    Log.d("API 수신 오류", "오류코드 : " + response.code() + ", 오류 메시지 : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
                Log.d("API 송신 오류 : ", "오류 메시지 : " + t.getMessage());
            }
        });
    }

    public void deleteSharedUsers(int folderId, int userId) {
        apiService.requestDeleteSharePermission(folderId, userId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "공유 삭제 성공");
                } else {
                    Log.e(getClass().getName(), "공유 삭제 실패 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(getClass().getName(), "서버 통신 실패: " + t.getMessage());
            }
        });
    }


}
