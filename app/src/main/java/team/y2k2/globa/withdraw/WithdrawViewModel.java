package team.y2k2.globa.withdraw;

import static team.y2k2.globa.api.ApiClient.authorization;
import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.WithdrawRequest;
import team.y2k2.globa.intro.IntroActivity;

public class WithdrawViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public WithdrawViewModel() {
        apiService = ApiClient.apiService;
    }

    public MutableLiveData<Integer> getResponseLiveData() {
        return responseLiveData;
    }

    public void withdrawUser(int surveyType, String content, Context context) {
        WithdrawRequest withdrawRequest = new WithdrawRequest(surveyType, content);

        apiService.requestWithdrawUser(APPLICATION_JSON, authorization, withdrawRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
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
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
                Log.d(getClass().getName(), "request 실패");
            }
        });
    }
}
