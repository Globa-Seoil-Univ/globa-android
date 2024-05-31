package team.y2k2.globa.main.folder.edit;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;

public class FolderNameEditViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderNameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public void folderRename(int folderId, FolderNameEditRequest folderNameEditRequest) {
        apiService.requestUpdateFolderName(folderId, "application/json", authorization, folderNameEditRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "폴더 이름 수정 완료");
                } else {
                    Log.d(getClass().getName(), "폴더 이름 수정 실패: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
                Log.d(getClass().getName(), "이름 수정 실패" + t.getMessage());
            }
        });
    }

}
