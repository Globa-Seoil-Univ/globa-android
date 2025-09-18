package team.y2k2.globa.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.login.LoginActivity;
import team.y2k2.globa.main.MainActivity;

public class IntroActivityModel extends ViewModel {

    private final MutableLiveData<Boolean> autoLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationPermissionGranted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> startButtonEnabled = new MutableLiveData<>();

    private IntroModel model;
    private UserApiClient userApiClient;

    private Context context;

    public static boolean isNofiGranted() {
        return IntroModel.isNofiGranted();
    }

    public void setContext(Context context) {
        this.context = context;
        model = new IntroModel(context);
        userApiClient = new UserApiClient(context);
        isLoading.setValue(true);
        startButtonEnabled.setValue(false);
    }

    public MutableLiveData<Boolean> getAutoLoginSuccess() {
        return autoLoginSuccess;
    }

    public MutableLiveData<Boolean> getNotificationPermissionGranted() {
        return notificationPermissionGranted;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getStartButtonEnabled() {
        return startButtonEnabled;
    }

    /**
     * 1. SharedPreferences에 Refresh Token이 있는지 확인합니다.
     * 2. 있다면, 서버에 새 Access Token을 요청합니다.
     * 3. 실패 시, 2초 간격으로 최대 3회까지 재시도합니다.
     * 4. 최종 성공 또는 실패 여부에 따라 LiveData를 업데이트합니다.
     */
    public void autoLogin() {
        Log.d(getClass().getName(), "자동 로그인을 시도합니다...");
        isLoading.setValue(true);

        if (model.hasRefreshToken()) {
            Log.d(getClass().getName(), "Refresh Token 발견. 서버에 새 토큰을 요청합니다.");
            CompletableFuture.supplyAsync(() -> {
                String refreshToken = model.getRefreshToken();
                if (refreshToken == null) {
                    return false;
                }

                int maxRetries = 3;
                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                    Log.d(getClass().getName(), "토큰 갱신 시도 #" + attempt);
                    TokenResponse tokenResponse = userApiClient.requestToken(new TokenRequest(refreshToken));

                    if (tokenResponse != null) {
                        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", tokenResponse.getAccessToken());
                        editor.putString("refreshToken", tokenResponse.getRefreshToken());
                        editor.commit();
                        Log.i(getClass().getName(), "새 토큰 저장 성공 (시도 #" + attempt + ").");
                        return true;
                    }

                    // 마지막 시도가 아니라면, 2초 대기 후 재시도합니다.
                    if (attempt < maxRetries) {
                        try {
                            Log.d(getClass().getName(), "토큰 갱신 실패. 2초 후 재시도합니다...");
                            Thread.sleep(2000); // 2초 대기
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // 스레드 중단 상태를 복원합니다.
                            Log.e(getClass().getName(), "재시도 대기 중 스레드가 중단되었습니다.", e);
                            return false; // 대기가 중단되면 즉시 실패 처리합니다.
                        }
                    }
                }

                // 모든 재시도 후에도 실패한 경우
                Log.e(getClass().getName(), "총 " + maxRetries + "번의 시도 후에도 토큰 갱신에 최종 실패했습니다.");
                return false;

            }).thenAccept(success -> {
                // 결과를 UI 스레드에서 처리합니다.
                if (success) {
                    Log.d(getClass().getName(), "자동 토큰 갱신 성공.");
                    autoLoginSuccess.postValue(true);
                } else {
                    if(model.hasRefreshToken()) {
                        autoLoginSuccess.postValue(true);
                        Log.d(getClass().getName(), "자동 토큰 갱신 실패. 이전 토큰으로 로그인 시도");
                        return;
                    }

                    Log.e(getClass().getName(), "자동 토큰 갱신 실패. 시작하기 버튼 활성화");
                    autoLoginSuccess.postValue(false);
                }
                // 성공 여부와 관계없이 로딩 상태를 종료합니다.
                isLoading.postValue(false);
                startButtonEnabled.postValue(!success);
            });
        } else {
            Log.d(getClass().getName(), "Refresh Token 없음. 로그인 화면으로 진행합니다.");
            autoLoginSuccess.setValue(false);
            isLoading.setValue(false);
            startButtonEnabled.setValue(true);
        }
    }

    public void navigateToMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void navigateToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void requestNotificationPermission(Activity activity) {
        model.requestNotificationPermission(activity);
    }

    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        model.handlePermissionResult(requestCode, permissions, grantResults);
    }
}

