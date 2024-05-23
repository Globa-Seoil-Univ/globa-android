package team.y2k2.globa.main.folder.permission;

import static team.y2k2.globa.api.ApiClient.authorization;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;

public class FolderPermissionViewModel extends ViewModel {

    private ApiService apiService;
    private ApiClient apiClient;
    private MutableLiveData<FolderPermissionResponse> usersLiveData;
    private MutableLiveData<String> errorLiveData;

    public FolderPermissionViewModel() {
        apiService = apiClient.getApiService();
        usersLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<FolderPermissionResponse> getUsersLiveData() {
        return usersLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchSharedUsers(String folderId, int page, int count) {
        apiService.requestFoloderShareUser(folderId, "application/json", authorization, page, count).enqueue(new Callback<FolderPermissionResponse>() {
            @Override
            public void onResponse(Call<FolderPermissionResponse> call, Response<FolderPermissionResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    usersLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Response failed");
                }
            }

            @Override
            public void onFailure(Call<FolderPermissionResponse> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

}
