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
     * ApiClient의 자동 토큰 갱신 메커니즘을 활용하여 자동 로그인을 시도합니다.
     * 간단한 인증 API(사용자 정보 조회)를 호출하여 토큰의 유효성을 검사합니다.
     * Access Token이 만료되었다면 ApiClient가 자동으로 갱신을 시도합니다.
     */
    public void autoLogin() {
        Log.d(getClass().getName(), "자동 로그인을 시도합니다...");
        isLoading.setValue(true);

        if (!model.hasRefreshToken()) {
            Log.d(getClass().getName(), "Refresh Token 없음. 로그인 화면으로 진행합니다.");
            autoLoginSuccess.postValue(false);
            isLoading.postValue(false);
            startButtonEnabled.postValue(true);
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            return userApiClient.requestUserInfo();
        }).thenAccept(userInfoResponse -> {
            boolean success = (userInfoResponse != null);

            if (success) {
                Log.d(getClass().getName(), "자동 로그인 성공. (토큰 유효성 검사 통과)");
            } else {
                Log.e(getClass().getName(), "자동 로그인 실패. (토큰 갱신 실패 또는 유효하지 않은 토큰)");
            }

            // 결과를 UI 스레드에서 처리합니다.
            autoLoginSuccess.postValue(success);
            isLoading.postValue(false);
            startButtonEnabled.postValue(!success);
        });
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
