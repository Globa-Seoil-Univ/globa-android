package team.y2k2.globa.main.folder.permission;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;

public class FolderPermissionActivityModel extends ViewModel {
    private final MutableLiveData<FolderPermissionResponse> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private ApiClient apiClient;

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public MutableLiveData<FolderPermissionResponse> getUsersLiveData() {
        return usersLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchSharedUsers(int folderId, int page, int count) {
        FolderPermissionResponse response = apiClient.requestFolderShareUser(folderId, page, count);
        usersLiveData.setValue(response);
    }

    public void changeSharedUsers(int folderId, int userId, String userRole) {
        Response<Void> response = apiClient.requestUpdateSharePermission(folderId, userId, userRole);

        if (response.isSuccessful()) {
            Log.d("API 수신 완료", "응답 코드 : " + response.code());
        } else {
            errorLiveData.setValue(response.message());
            Log.d("API 수신 오류", "오류코드 : " + response.code() + ", 오류 메시지 : " + response.message());
        }
    }

    public void deleteSharedUsers(int folderId, int userId) {
        Response<Void> response = apiClient.requestDeleteSharePermission(folderId, userId);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "공유 삭제 성공");
        } else {
            errorLiveData.setValue(response.message());
        }
    }
}
