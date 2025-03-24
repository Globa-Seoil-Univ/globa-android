package team.y2k2.globa.docs.move;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsMoveModel extends ViewModel {
    private final MutableLiveData<FolderResponse> folderResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moveSuccessLiveData = new MutableLiveData<>();
    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;

    public LiveData<FolderResponse> getFolderResponseLiveData() {
        return folderResponseLiveData;
    }

    public void setApiClient(Context context) {
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient();
    }

    public LiveData<Boolean> getMoveSuccessLiveData() {
        return moveSuccessLiveData;
    }

    public void loadFolders() {
        FolderResponse response = folderApiClient.requestGetFolders(1, 100);
        folderResponseLiveData.setValue(response);
    }

    public void moveDocs(String currentFolderId, String recordId, int selectedFolderPosition, FolderResponse folderResponse) {
        if (folderResponse != null && folderResponse.getFolders() != null && !folderResponse.getFolders().isEmpty()) {
            Folder targetFolder = folderResponse.getFolders().get(selectedFolderPosition);
            String targetFolderId = String.valueOf(targetFolder.getFolderId());

            if (recordApiClient.requestUpdateDocsMove(currentFolderId, recordId, targetFolderId).isSuccessful())
                moveSuccessLiveData.setValue(true);
        } else {
            moveSuccessLiveData.setValue(false);
        }
    }
}