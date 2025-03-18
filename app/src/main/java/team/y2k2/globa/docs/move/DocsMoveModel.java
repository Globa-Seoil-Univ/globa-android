package team.y2k2.globa.docs.move;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsMoveModel extends ViewModel {
    private MutableLiveData<FolderResponse> folderResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> moveSuccessLiveData = new MutableLiveData<>();
    private ApiClient apiClient;

    public LiveData<FolderResponse> getFolderResponseLiveData() {
        return folderResponseLiveData;
    }

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public LiveData<Boolean> getMoveSuccessLiveData() {
        return moveSuccessLiveData;
    }

    public void loadFolders() {
        FolderResponse response = apiClient.requestGetFolders(1, 100);
        folderResponseLiveData.setValue(response);
    }

    public void moveDocs(String currentFolderId, String recordId, int selectedFolderPosition, FolderResponse folderResponse) {
        if (folderResponse != null && folderResponse.getFolders() != null && !folderResponse.getFolders().isEmpty()) {
            Folder targetFolder = folderResponse.getFolders().get(selectedFolderPosition);
            String targetFolderId = String.valueOf(targetFolder.getFolderId());

            if(apiClient.requestUpdateDocsMove(currentFolderId, recordId, targetFolderId).isSuccessful())
                moveSuccessLiveData.setValue(true);
        } else {
            moveSuccessLiveData.setValue(false);
        }
    }
}