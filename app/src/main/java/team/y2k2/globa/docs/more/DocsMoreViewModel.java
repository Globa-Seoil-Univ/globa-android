package team.y2k2.globa.docs.more;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;

public class DocsMoreViewModel extends ViewModel {

    private ApiService apiService;
    private ApiClient apiClient;
    private MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public DocsMoreViewModel() {
        apiService = apiClient.getApiService();
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void deleteDocs(String folderId, String recordId) {
        apiService.requestDeleteRecord(folderId, recordId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(getClass().getName(), "문서 삭제 성공 : " + response.code());
                    isDeleted.setValue(true);
                } else {
                    Log.d(getClass().getName(), "문서 삭제 실패 : " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getName(), "문서 삭제 요청 실패 : " + t.getMessage());
            }
        });
    }


}
