package team.y2k2.globa.main.folder.share;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserSearchResponse;

public class FolderShareActivityModel extends ViewModel {

    private FolderApiClient folderApiClient;
    private UserApiClient userApiClient;
    private final MutableLiveData<UserSearchResponse> userSearchLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> isSucceedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        folderApiClient = new FolderApiClient();
        userApiClient = new UserApiClient();
    }

    public LiveData<UserSearchResponse> getUserSearchLiveData() {
        return userSearchLiveData;
    }

    public MutableLiveData<String> getIsSucceedLiveData() {
        return isSucceedLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void searchUserInfo(String userCode) {
        UserSearchResponse response = userApiClient.requestSearchUserInfo(userCode);
        userSearchLiveData.postValue(response);
    }

    public void addSharedUser(int folderId, int userId, String role) {
        Response<Void> response = folderApiClient.requestInsertFolderShareUser(folderId, userId, role);
        isSucceedLiveData.setValue(String.valueOf(response.code()));
    }

}
