package team.y2k2.globa.docs.more;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;

public class DocsMoreActivityModel extends ViewModel {
    private ApiClient apiClient;
    private final MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void deleteDocs(String folderId, String recordId) {
        Response<Void> response = apiClient.deleteRecord(folderId,recordId);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "문서 삭제 성공 : " + response.code());
            isDeleted.setValue(true);
        }
        else {
            Log.d(getClass().getName(), "문서 삭제 실패 : " + response.code() + ", " + response.message());
        }
    }
}
