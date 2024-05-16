package team.y2k2.globa.withdraw;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiService;

public class WithdrawViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public WithdrawViewModel() {
        apiService = ApiClient.getApiService();
    }
    
    public void withdrawUser(String authorization, int surveyType, String content) {
        try {
            apiService.requestWithdrawUser("application/json", authorization, surveyType, content);
            Log.d(getClass().getName(), "회원탈퇴 요청 성공");
        } catch(Exception e) {
            errorLiveData.setValue("요청 전송 오류 발생");
            Log.d(getClass().getName(), "오류발생 : " + e.getMessage());
        }
    }


}
