package team.y2k2.globa.main.folder.edit;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiService;

public class FolderNameEditViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FolderNameEditViewModel() {
        apiService = ApiClient.getApiService();
    }

    public void folderRename(String authorization, String title) {
        try {
//            apiService.requestUpdateFolderName("application/json", authorization, title);
            Log.d(getClass().getName(), "회원 탈퇴 요청 성공");
        } catch(Exception e) {
            errorLiveData.setValue("요청 전송 오류 발생");
            Log.d(getClass().getName(), "오류 발생 : " + e.getMessage());
        }
    }

}
