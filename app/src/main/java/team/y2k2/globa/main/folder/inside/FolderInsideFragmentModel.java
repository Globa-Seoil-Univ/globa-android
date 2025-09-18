package team.y2k2.globa.main.folder.inside;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.FolderInsideRecord;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.sql.RecordDB;

public class FolderInsideFragmentModel extends ViewModel {
    private RecordApiClient recordApiClient;
    private FolderApiClient folderApiClient;
    private RecordDB recordDB;

    private final MutableLiveData<List<FolderInsideRecord>> folderInsideRecords = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteResponseCode = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> folderTitle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isListEmpty = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setApiClient(Context context) {
        folderApiClient = new FolderApiClient();
        recordApiClient = new RecordApiClient(context);
        recordDB = new RecordDB(context);
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
    public LiveData<Boolean> getIsListEmpty() {
        return isListEmpty;
    }

    public void fetchFolderInsideRecords(int folderId) {
        isLoading.setValue(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            FolderInsideRecordResponse response = recordApiClient.requestGetFolderInside(folderId, 1, 100);

            handler.post(() -> {
                isLoading.setValue(false);
                if (response == null || response.getRecords() == null || response.getRecords().isEmpty()) {
                    isListEmpty.setValue(true);
                    folderInsideRecords.setValue(new java.util.ArrayList<>());
                } else {
                    isListEmpty.setValue(false);
                    folderInsideRecords.setValue(response.getRecords());
                }
            });
        });
    }
    public void deleteFolder(int folderId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            Response<Void> response = folderApiClient.requestDeleteFolder(folderId);
            deleteResponseCode.postValue(response.code());
        });
    }

    public void deleteDocs(String folderId, String recordId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Response<Void> response = recordApiClient.deleteRecord(folderId, recordId);

            if (response.isSuccessful()) {
                Log.d(getClass().getName(), "문서 삭제 성공 (API) : " + response.code());

                if (recordDB != null) {
                    recordDB.deleteRecordById(recordId);
                    Log.d(getClass().getName(), "문서 삭제 성공 (Local DB)");
                }

                fetchFolderInsideRecords(Integer.parseInt(folderId));
            } else {
                Log.d(getClass().getName(), "문서 삭제 실패 : " + response.code() + ", " + response.message());
            }
        });
    }
}