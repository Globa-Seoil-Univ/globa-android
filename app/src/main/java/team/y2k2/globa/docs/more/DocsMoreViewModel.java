package team.y2k2.globa.docs.more;

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
    private String authorization;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _closeActivities = new MutableLiveData<>();
    public LiveData<Boolean> closeActivities = _closeActivities;
    private Context context;

    public DocsMoreViewModel(Context context) {
        this.context = context;
        apiClient = new ApiClient(context);
        apiService = apiClient.getApiService();
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void triggerCloseActivities() {
        _closeActivities.setValue(true);
    }

    public void deleteDocs(String folderId, String recordId) {
        apiService.requestDeleteRecord(folderId, recordId, "application/json", authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(getClass().getName(), "문서 삭제 성공");
                    Toast.makeText(context, "문서 삭제 성공", Toast.LENGTH_SHORT).show();
                    triggerCloseActivities();

                } else {
                    Log.d(getClass().getName(), "문서 삭제 실패");
                    Toast.makeText(context, "문서 삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(getClass().getName(), "문서 삭제 요청 실패");
                Toast.makeText(context, "문서 삭제 요청 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
