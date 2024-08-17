package team.y2k2.globa.withdraw;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.WithdrawRequest;
import team.y2k2.globa.intro.IntroActivity;

public class WithdrawViewModel extends ViewModel {

    private ApiService apiService;
    private ApiClient apiClient;
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public WithdrawViewModel() {
        apiService = ApiClient.apiService;
    }
    
    public void withdrawUser(int[] surveyType, String content) {
        WithdrawRequest withdrawRequest = new WithdrawRequest(surveyType, content);
        apiService.requestWithdrawUser("application/json", authorization, withdrawRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    editor.clear();
                    editor.apply();
                    Intent withDrawIntent = new Intent(context, IntroActivity.class);
                    context.startActivity(withDrawIntent);
                    Log.d(getClass().getName(), "로그아웃 성공");
                } else {
                    Log.d(getClass().getName(), "로그아웃 실패 : " + response.code());
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
