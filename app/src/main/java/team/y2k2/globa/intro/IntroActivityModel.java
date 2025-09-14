package team.y2k2.globa.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.login.LoginActivity;
import team.y2k2.globa.main.MainActivity;

public class IntroActivityModel extends ViewModel {

    private final MutableLiveData<Boolean> autoLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationPermissionGranted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> startButtonEnabled = new MutableLiveData<>();

    private IntroModel model;

    private Context context;

    public static boolean isNofiGranted() {
        return IntroModel.isNofiGranted();
    }

    public void setContext(Context context) {
        this.context = context;
        model = new IntroModel(context);
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

    public void autoLogin() {
        Log.d(getClass().getName(), "자동 로그인을 시도합니다...");
        if (model.checkAutoLogin()) {
            Log.d(getClass().getName(), "자동 로그인 조건 충족 (Refresh Token 존재).");
            autoLoginSuccess.setValue(true);
        } else {
            Log.d(getClass().getName(), "자동 로그인 조건 불충족. 로그인 화면으로 진행합니다.");
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
