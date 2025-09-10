package team.y2k2.globa.docs.more;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.sql.RecordDB;

public class DocsMoreActivityModel extends ViewModel {
    private RecordApiClient apiClient;
    private RecordDB recordDB;
    private final MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new RecordApiClient();
        this.recordDB = new RecordDB(context);
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void deleteDocs(String folderId, String recordId) {
        Response<Void> response = apiClient.deleteRecord(folderId, recordId);

        if (response.isSuccessful()) {
            Log.d(getClass().getName(), "문서 삭제 성공 (API) : " + response.code());

            if (recordDB != null) {
                recordDB.deleteRecordById(recordId);
                Log.d(getClass().getName(), "문서 삭제 성공 (Local DB)");
            }

            isDeleted.setValue(true);
        } else {
            Log.e(getClass().getName(), "문서 삭제 실패 (API) : " + response.code());
            errorLiveData.postValue("서버에서 문서를 삭제하지 못했습니다.");
        }
    }
}