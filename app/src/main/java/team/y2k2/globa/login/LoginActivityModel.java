package team.y2k2.globa.login;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.util.login.SnsLoginManager;

public class LoginActivityModel extends ViewModel implements SnsLoginManager.SnsLoginCallback {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private UserApiClient userApiClient;
    private SnsLoginManager snsLoginManager;
    private UserPreferencesManager preferencesManager;
    private Activity activity;

    public void setContext(Activity activity) {
        this.activity = activity;
        this.userApiClient = new UserApiClient();
        this.snsLoginManager = new SnsLoginManager(activity, this);
        this.preferencesManager = new UserPreferencesManager(activity);
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void startSignIn(int signInType) {
        loading.setValue(true);
        snsLoginManager.startSignIn(signInType);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginModel.RC_GOOGLE) {
            snsLoginManager.handleGoogleSignInResult(data);
        }
    }

    @Override
    public void onSuccess(LoginModel model, String token) {
        LoginRequest request = new LoginRequest(model, IntroActivity.isNotificationGranted(), token);
        LoginResponse response = userApiClient.requestSignIn(request);

        if (response == null) {
            errorMessage.postValue("로그인 실패 : 탈퇴한 사용자");
            loading.postValue(false);
        } else {
            preferencesManager.saveLoginInfo(request, response);
            sendLogMessage(request, response);

            UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();
            preferencesManager.saveUserProfile(userInfoResponse);
            showLogMessages(userInfoResponse);

            loading.postValue(false);
            loginSuccess.postValue(true);
        }
    }

    @Override
    public void onError(String errorMessage) {
        this.errorMessage.postValue(errorMessage);
        loading.postValue(false);
    }

    private void sendLogMessage(LoginRequest request, LoginResponse response) {
        // 로그 메시지 전송 로직
    }

    private void showLogMessages(UserInfoResponse response) {
        // 로그 메시지 표시 로직
    }
}