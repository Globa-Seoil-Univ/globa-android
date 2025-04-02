package team.y2k2.globa.main;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class FolderFragmentModel extends ViewModel {

    private MutableLiveData<List<Folder>> folders = new MutableLiveData<>();
    public LiveData<List<Folder>> getFolders() {
        return folders;
    }

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public LiveData<Boolean> getLoading() { return loading; }

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() { return errorMessage; }

    private FolderApiClient folderApiClient = new FolderApiClient();

    public FolderFragmentModel() {
        loadFolders();
    }

    public void loadFolders() {
        loading.setValue(true);

        // Use a background thread (e.g., using Kotlin coroutines or Java Executors)
        new Thread(() -> {
            FolderResponse response = folderApiClient.requestGetFolders(1, 100);
            loading.postValue(false); // Loading complete (on main thread)

            if (response != null && response.getFolders() != null) {
                folders.postValue(response.getFolders()); // Update LiveData on main thread
            } else {
                errorMessage.postValue("Failed to load folders.");
                Log.e(getClass().getSimpleName(), "Failed to load folders.");
            }
        }).start();
    }

    public void deleteFolder(int folderId) {
        new Thread(() -> {
            folderApiClient.requestDeleteFolder(folderId);
            loadFolders(); // Refresh the folder list after deletion
        }).start();
    }
}