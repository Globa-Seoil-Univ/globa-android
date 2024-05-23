package team.y2k2.globa.main.folder.edit;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;

public class FolderNameEditViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderNameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public void folderRename(String folderId, String authorization, String title) {
        apiService.requestUpdateFolderName(folderId, "application/json", authorization, title).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "폴더 이름 수정 완료");
                } else {
                    Log.d(getClass().getName(), "폴더 이름 수정 실패");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

}
