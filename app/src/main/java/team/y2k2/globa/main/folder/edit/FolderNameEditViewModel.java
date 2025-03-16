package team.y2k2.globa.main.folder.edit;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;

public class FolderNameEditViewModel extends ViewModel {
    private final ApiService apiService;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<String> folderName = new MutableLiveData<>("");
    public MutableLiveData<Integer> textCount = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> isConfirmEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>(false);
    private int folderId;

    public FolderNameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public void setFolderName(String folderName) {
        this.folderName.setValue(folderName);
        updateTextCount(folderName.length());
        updateConfirmButtonState(folderName.length());
    }

    private void updateTextCount(int length) {
        textCount.setValue(length);
    }

    private void updateConfirmButtonState(int length) {
        isConfirmEnabled.setValue(length > 0);
    }

    public void folderRename(int folderId, FolderNameEditRequest folderNameEditRequest) {
        apiService.requestUpdateFolderName(folderId, "application/json", authorization, folderNameEditRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API 수신 완료", "폴더 이름 변경 성공: " + response.code());
                    finishActivity.setValue(true);
                } else {
                    switch (response.code()) {
                        case 40110 :
                            Log.d("API 수신 오류", "유효하지 않은 토큰" + response.code());
                            errorLiveData.setValue("폴더 이름 변경 실패");
                            break;
                        case 40120 :
                            Log.d("API 수신 오류", "토큰 파싱 실패" + response.code());
                            errorLiveData.setValue("토큰 파싱 실패");
                            break;
                        case 40130 :
                            Log.d("API 수신 오류", "만료된 갱신 토큰" + response.code());
                            errorLiveData.setValue("만료된 갱신 토큰");
                            break;
                        case 40140 :
                            Log.d("API 수신 오류", "유효하지 않은 SNS 토큰" + response.code());
                            errorLiveData.setValue("유효하지 않은 SNS 토큰");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
                Log.d("API 송신 오류", "폴더 이름 변경 실패" + t.getMessage());
            }
        });
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public void onBackButtonClicked() {
        finishActivity.setValue(true);
    }

    public void onCancelButtonClicked() {
        folderName.setValue("");
    }

    public void onConfirmButtonClicked() {
        FolderNameEditRequest request = new FolderNameEditRequest(folderName.getValue());
        folderRename(folderId, request);
    }
}