package team.y2k2.globa.main.folder.inside;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Response;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;

public class FolderInsideFragmentModel extends ViewModel {
    RecordApiClient recordApiClient;
    FolderApiClient folderApiClient;
    private final MutableLiveData<List<FolderInsideRecord>> folderInsideRecords = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteResponseCode = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> folderTitle = new MutableLiveData<>();

    public void setApiClient(Context context) {
        recordApiClient = new RecordApiClient();
        folderApiClient = new FolderApiClient();
    }

    public LiveData<String> getFolderTitle() {
        return folderTitle;
    }

    public void setFolderTitle(String title) {
        folderTitle.setValue(title);
    }

    public LiveData<List<FolderInsideRecord>> getFolderInsideRecords() {
        return folderInsideRecords;
    }

    public LiveData<Integer> getDeleteResponseCode() {
        return deleteResponseCode;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchFolderInsideRecords(int folderId) {
        FolderInsideRecordResponse response = recordApiClient.requestGetFolderInside(folderId, 1, 100);
        folderInsideRecords.setValue(response.getRecords());
    }

    public void deleteFolder(int folderId) {
        folderApiClient.requestDeleteFolder(folderId);
    }

    public void deleteDocs(String folderId, String recordId) {
        Response<Void> response = recordApiClient.deleteRecord(folderId, recordId);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "문서 삭제 성공 : " + response.code());
        } else {
            Log.d(getClass().getName(), "문서 삭제 실패 : " + response.code() + ", " + response.message());
        }
    }
}