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
import com.kakao.sdk.auth.model.OAuthToken;

import java.util.ArrayList;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.main.MainActivity;

public class LoginViewModel extends ViewModel {
    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static class LoginListener implements OnCompleteListener<AuthResult>{
        Context context;
        FirebaseAuth mAuth;
        String accessToken;
        OAuthToken token;

        String uid;
        String name;
        String profile;

        public LoginListener(Context context, FirebaseAuth mAuth, String accessToken) {
            this.context = context;
            this.mAuth = mAuth;
            this.accessToken = accessToken;
        }

        public LoginListener(String uid, String name, String profile, Context context) {
            this.uid = uid;
            this.name = name;
            this.profile = profile;
            this.context = context;
        }

        public void KakaoLogin() {
            ApiClient apiClient = new ApiClient(context);

            LoginRequest request = new LoginRequest(RC_KAKAO, uid, name, profile, true);

            LoginResponse response = apiClient.requestSignIn(request);
            userPreferences(request, response);
            sendLogMessage(request,response);

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Firebase 로그인 성공
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();
                String name = user.getDisplayName();
                String profile = user.getPhotoUrl().toString();

                ApiClient apiClient = new ApiClient(context);

                LoginRequest request = new LoginRequest(RC_GOOGLE, uid, name, profile, true);

                LoginResponse response = apiClient.requestSignIn(request);
                userPreferences(request, response);
                sendLogMessage(request,response);

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

                Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "로그인 실패| 앱을 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
            }
        }

        public void userPreferences(LoginRequest request, LoginResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("accessToken", accessToken);
            editor.putString("refreshToken", refreshToken);
            editor.putString("uid", request.getSnsId());
            editor.putString("name", request.getName());
            editor.putString("profile", request.getProfile());
            editor.commit();
        }

        public void sendLogMessage(LoginRequest request, LoginResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            Log.d(getClass().getName(), "로그인 성공");
            Log.d("엑세스 토큰", "토큰: " + accessToken);

            ArrayList list = new ArrayList();
            list.add("accessToken : " + accessToken);
            list.add("refreshToken: " + refreshToken);
            list.add("snsId       : " + request.getSnsId());
            list.add("name        : " + request.getName());
            list.add("profile     : " + request.getProfile());
        }
    }
}