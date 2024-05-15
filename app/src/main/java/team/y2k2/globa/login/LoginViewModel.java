package team.y2k2.globa.login;

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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
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

        private final int RC_KAKAO = 1001;
        private final int RC_NAVER = 1002;
        private final int RC_TWITTER = 1003;
        private final int RC_GOOGLE = 1004;

        public LoginListener(Context context, FirebaseAuth mAuth, String accessToken) {
            this.context = context;
            this.mAuth = mAuth;
            this.accessToken = accessToken;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Firebase 로그인 성공
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();
                String name = user.getDisplayName();
                String profile = user.getPhotoUrl().toString();

                LoginRequest loginRequest = new LoginRequest(RC_GOOGLE, uid, name, profile, true);

                // Retrofit 인스턴스 생성
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiService.API_BASE_URL) // 외부 접근 시 API_BASE_URL 로 변경
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                Call<LoginResponse> call = apiService.requestSignIn(loginRequest);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 로그인 성공
                            LoginResponse loginResponse = response.body();
                            userPreferences(loginRequest, loginResponse);
                            sendLogMessage(loginRequest,loginResponse);

                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);

                            Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // 요청 실패
                        Toast.makeText(context, "서비스 오류가 발생했습니다." + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("LOGINFAILED", t.getMessage());
                    }
                });
            } else {
                // Firebase 로그인 실패
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

            ArrayList list = new ArrayList();
            list.add("accessToken : " + accessToken);
            list.add("refreshToken: " + refreshToken);
            list.add("snsId       : " + request.getSnsId());
            list.add("name        : " + request.getName());
            list.add("profile     : " + request.getProfile());
        }
    }
}