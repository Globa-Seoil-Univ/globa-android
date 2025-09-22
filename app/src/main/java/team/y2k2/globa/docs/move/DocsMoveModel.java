package team.y2k2.globa.docs.move;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsMoveModel extends ViewModel {
    private final MutableLiveData<FolderResponse> folderResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moveSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isMoving = new MutableLiveData<>(false);

    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;

    public LiveData<FolderResponse> getFolderResponseLiveData() {
        return folderResponseLiveData;
    }

    public void setApiClient(Context context) {
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient(context);
    }

    public LiveData<Boolean> getMoveSuccessLiveData() {
        return moveSuccessLiveData;
    }

    public LiveData<Boolean> getIsMoving() {
        return isMoving;
    }

    public void loadFolders() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            FolderResponse response = folderApiClient.requestGetFolders(1, 100);
            handler.post(() -> folderResponseLiveData.setValue(response));
        });
    }

    public void moveDocs(String currentFolderId, String recordId, int selectedFolderPosition, FolderResponse folderResponse) {
        isMoving.setValue(true);


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean success = false;
            if (folderResponse != null && folderResponse.getFolders() != null && !folderResponse.getFolders().isEmpty()) {
                Folder targetFolder = folderResponse.getFolders().get(selectedFolderPosition);
                String targetFolderId = String.valueOf(targetFolder.getFolderId());

                if (recordApiClient.requestUpdateDocsMove(currentFolderId, recordId, targetFolderId).isSuccessful()) {
                    success = true;
                }
            }

            boolean finalSuccess = success;
            handler.post(() -> {
                moveSuccessLiveData.setValue(finalSuccess);
                isMoving.setValue(false);
            });
        });
    }
}