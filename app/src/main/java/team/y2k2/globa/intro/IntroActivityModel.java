package team.y2k2.globa.intro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.login.LoginActivity;
import team.y2k2.globa.main.MainActivity;

public class IntroActivityModel extends ViewModel {

    private MutableLiveData<Boolean> autoLoginSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> notificationPermissionGranted = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> startButtonEnabled = new MutableLiveData<>();

    private IntroModel model;

    private Context context;

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
        if (model.checkAutoLogin()) {
            autoLoginSuccess.setValue(true);
        } else {
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

    public static boolean isNofiGranted() {
        return IntroModel.isNofiGranted();
    }
}