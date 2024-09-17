package team.y2k2.globa.login;

import static team.y2k2.globa.login.LoginModel.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.user.model.User;

import java.util.ArrayList;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.main.MainActivity;

public class LoginViewModel extends ViewModel {
    Activity activity;

    LoginModel model;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void autoLogin(String refreshToken) {

    }

    public static class LoginListener implements OnCompleteListener<AuthResult> {
        ApiClient apiClient;
        LoginModel model;
        LoginActivity activity;
        FirebaseAuth mAuth;
        String accessToken;
        String refreshToken;

        public LoginListener(LoginActivity activity, FirebaseAuth mAuth, String accessToken) {
            this.activity = activity;
            this.mAuth = mAuth;
            this.accessToken = accessToken;

            apiClient = new ApiClient(activity);
        }

        public LoginListener(LoginActivity activity, String refreshToken) {
            this.activity = activity;
            this.refreshToken = refreshToken;

            apiClient = new ApiClient(activity);
        }

        public LoginListener(User user, LoginActivity activity) {
            this.activity = activity;
            this.model = new LoginModel(user, RC_KAKAO);

            apiClient = new ApiClient(activity);
        }

        public void autoLogin() {
            TokenRequest request = new TokenRequest(refreshToken);
            TokenResponse response = apiClient.requestToken(request);

            if(response == null)
                return;

            userPreferences(request, response);
            sendLogMessage(request,response);

            activity.dialog.dismiss();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);

            Toast.makeText(activity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

        }

        public void KakaoLogin(String token) {
            LoginRequest request = new LoginRequest(model, true, token);
            LoginResponse response = apiClient.requestSignIn(request);

            userPreferences(request, response);
            sendLogMessage(request,response);

            activity.dialog.dismiss();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);

            Toast.makeText(activity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Firebase 로그인 성공

                FirebaseUser user = mAuth.getCurrentUser();
                user.getIdToken(false).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        String idToken = task2.getResult().getToken();
                        // 여기에서 새로운 ID 토큰을 처리합니다.
                        Log.d("ID_TOKEN", "ID Token: " + idToken);

                        model = new LoginModel(mAuth.getCurrentUser(), RC_GOOGLE, idToken);
                        LoginRequest request = new LoginRequest(model, true, idToken);

                        LoginResponse response = apiClient.requestSignIn(request);
                        userPreferences(request, response);
                        sendLogMessage(request,response);

                        activity.dialog.dismiss();
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);

                        Toast.makeText(activity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toast.makeText(activity, "로그인 실패 : " + task.getResult(), Toast.LENGTH_SHORT).show();
            }
        }

        public void userPreferences(TokenRequest request, TokenResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            Log.d("엑세스 토큰", "AT : " + accessToken);

            SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("accessToken", accessToken);
            editor.putString("refreshToken", refreshToken);
            editor.commit();
        }

        public void userPreferences(LoginRequest request, LoginResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            Log.d("엑세스 토큰", "AT : " + accessToken);

            SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("accessToken", accessToken);
            editor.putString("refreshToken", refreshToken);
            editor.putString("uid", request.getSnsId());
            editor.putString("name", request.getName());
            editor.putString("profile", request.getProfile());
            editor.commit();
        }

        public void sendLogMessage(TokenRequest request, TokenResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            ArrayList list = new ArrayList();
            list.add("accessToken : " + accessToken);
            list.add("refreshToken: " + refreshToken);
        }

        public void sendLogMessage(LoginRequest request, LoginResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            ArrayList list = new ArrayList();
            list.add("accessToken : " + accessToken);
            list.add("refreshToken: " + refreshToken);
            list.add("snsId       : " + request.getSnsId());
            list.add("name        : " + request.getName());
            list.add("profile     : " + request.getProfile());
        }
    }

    public String getAppKeyForKakao() {
        return model.APP_KEY_KAKAO;
    }
}