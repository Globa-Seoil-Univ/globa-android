package team.y2k2.globa.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.util.login.SnsLoginManager;

public class LoginActivityModel extends ViewModel implements SnsLoginManager.SnsLoginCallback {
    private static final String TAG = "LoginActivityModel";
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
        Log.d(TAG, "ViewModel이 초기화되고 Context가 설정되었습니다.");
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
        Log.d(TAG, "로그인을 시작합니다. Type: " + signInType);
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
        CompletableFuture.runAsync(() -> {
            try {
                Log.i(TAG, "onSuccess: SnsLoginManager로부터 성공 콜백을 받았습니다.");
                Log.i(TAG, "onSuccess: 발급받은 토큰: " + token);

                LoginRequest request = new LoginRequest(model, IntroActivity.isNotificationGranted(), token);

                Log.d(TAG, "onSuccess: 서버로 로그인 요청을 보냅니다. Request: " + request.toString());
                LoginResponse response = userApiClient.requestSignIn(request);

                sendLogMessage(request, response);

                if (response == null) {
                    Log.w(TAG, "onSuccess: 서버로부터 받은 응답이 null입니다. 탈퇴한 사용자이거나 서버 오류일 수 있습니다.");
                    errorMessage.postValue("로그인 실패 : 탈퇴한 사용자");
                } else {
                    Log.i(TAG, "onSuccess: 서버로부터 성공적인 응답을 받았습니다. Response: " + response.toString());
                    preferencesManager.saveLoginInfo(request, response);

                    UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();

                    if (userInfoResponse != null) {
                        preferencesManager.saveUserProfile(userInfoResponse);
                    } else {
                        Log.e(TAG, "onSuccess: 사용자 정보 조회에 실패했으나 로그인은 계속 진행합니다.");
                    }

                    // 로그인 성공 상태를 UI 스레드로 전달
                    loginSuccess.postValue(true);
                }
            } catch (Exception e) {
                Log.e(TAG, "로그인 처리 중 에러 발생", e);
                errorMessage.postValue("알 수 없는 오류가 발생했습니다.");
            } finally {
                // 모든 작업이 끝나면 로딩 상태를 UI 스레드로 전달
                loading.postValue(false);
            }
        });
    }

    @Override
    public void onError(String errorMsg) {
        Log.e(TAG, "onError: SnsLoginManager로부터 에러 콜백을 받았습니다.");
        Log.e(TAG, "onError: 에러 메시지: " + errorMsg);

        this.errorMessage.postValue(errorMsg);
        loading.postValue(false);
    }

    private void sendLogMessage(LoginRequest request, LoginResponse response) {
        Log.d(TAG, "sendLogMessage -> snsKind:" + request.getSnsKind());
        Log.d(TAG, "sendLogMessage -> snsId: " + request.getSnsId());
        Log.d(TAG, "sendLogMessage -> name: " + request.getName());
        Log.d(TAG, "sendLogMessage -> token: " + request.getToken());
        Log.d(TAG, "sendLogMessage -> profile: " + request.getProfile());
    }

    private void showLogMessages(UserInfoResponse response) {
        if(response != null) {
            Log.d(TAG, "showLogMessages -> name: " + response.getName());
            Log.d(TAG, "showLogMessages -> userId: " + response.getUserId());
        }
    }
}

