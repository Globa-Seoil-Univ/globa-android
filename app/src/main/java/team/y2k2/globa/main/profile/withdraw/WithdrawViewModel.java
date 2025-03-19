package team.y2k2.globa.main.profile.withdraw;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.intro.IntroActivity;

public class WithdrawViewModel extends ViewModel {

    private ApiClient apiClient;
    private final MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public MutableLiveData<Integer> getResponseLiveData() {
        return responseLiveData;
    }

    public void withdrawUser(int surveyType, String content, Context context) {
        Response<Void> response = apiClient.requestWithdrawUser(surveyType, content);

        if (response.isSuccessful()) {
            Intent withDrawIntent = new Intent(context, IntroActivity.class);
            withDrawIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(withDrawIntent);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notice");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("event");
            Toast.makeText(context, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "회원 탈퇴 성공");
            responseLiveData.setValue(response.code());
        } else {
            Log.d(getClass().getName(), "회원 탈퇴 실패 : " + response.code());
            errorLiveData.setValue(response.message());

        }
    }
}
