package team.y2k2.globa.main.folder.edit;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;

public class FolderNameEditActivityModel extends ViewModel {

    private FolderApiClient apiClient;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>(false);
    public MutableLiveData<String> folderName = new MutableLiveData<>("");
    public MutableLiveData<Integer> textCount = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> isConfirmEnabled = new MutableLiveData<>(false);
    private int folderId;

    public void setApiClient(Context context) {
        this.apiClient = new FolderApiClient();
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
        Response<Void> response = apiClient.requestUpdateFolderName(folderId, folderNameEditRequest);

        if (response.isSuccessful()) {
            Log.d("API 수신 완료", "폴더 이름 변경 성공: " + response.code());
            finishActivity.setValue(true);
        }
        else {
            switch (response.code()) {
                case 40110:
                    Log.d("API 수신 오류", "유효하지 않은 토큰" + response.code());
                    errorLiveData.setValue("폴더 이름 변경 실패");
                    break;
                case 40120:
                    Log.d("API 수신 오류", "토큰 파싱 실패" + response.code());
                    errorLiveData.setValue("토큰 파싱 실패");
                    break;
                case 40130:
                    Log.d("API 수신 오류", "만료된 갱신 토큰" + response.code());
                    errorLiveData.setValue("만료된 갱신 토큰");
                    break;
                case 40140:
                    Log.d("API 수신 오류", "유효하지 않은 SNS 토큰" + response.code());
                    errorLiveData.setValue("유효하지 않은 SNS 토큰");
                    break;
            }
        }
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