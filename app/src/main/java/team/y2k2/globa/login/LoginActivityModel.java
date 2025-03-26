package team.y2k2.globa.login;

import static team.y2k2.globa.login.LoginModel.*;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.intro.IntroActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivityModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private LoginModel model;
    public team.y2k2.globa.api.clients.UserApiClient userApiClient;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginActivity activity;
    private final Function2<OAuthToken, Throwable, Unit> signInKakaoCallback = (token, error) -> {
        if (error != null) {
            errorMessage.postValue("Kakao sign-in failed: " + error.getMessage());
            loading.postValue(false);
        } else if (token != null) {
            handleKakaoSignInResult(token);
        }
        return null;
    };

    public void setContext(LoginActivity activity) {
        this.activity = activity;
        userApiClient = new team.y2k2.globa.api.clients.UserApiClient();
        mAuth = FirebaseAuth.getInstance();
        initGoogleSignInClient();
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

    private void initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(activity.getString(R.string.default_web_client_id)).requestServerAuthCode(activity.getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public String getAppKeyForKakao() {
        return APP_KEY_KAKAO;
    }

    public void startSignIn(int signInType) {
        loading.setValue(true);
        if (signInType == RC_GOOGLE) {
            signInGoogle();
        } else if (signInType == RC_KAKAO) {
            signInKakao();
        }
    }

    public void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_GOOGLE);
    }

    public void signInKakao() {
        UserApiClient userApiClient = UserApiClient.getInstance();
        if (userApiClient.isKakaoTalkLoginAvailable(activity)) {
            userApiClient.loginWithKakaoTalk(activity, signInKakaoCallback);
        } else {
            userApiClient.loginWithKakaoAccount(activity, signInKakaoCallback);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            signInGoogleWithFirebase(account);
        } catch (ApiException e) {
            errorMessage.postValue("Google sign-in failed: " + e.getMessage());
            loading.postValue(false);
        }
    }

    private void signInGoogleWithFirebase(GoogleSignInAccount acct) {
        String accessToken = acct.getIdToken();
        String authCode = acct.getServerAuthCode();

        if (accessToken == null) {
            errorMessage.postValue("Google Access Token is null");
            loading.postValue(false);
            return;
        }

        Log.d(getClass().getSimpleName(), "구글 AT: " + accessToken);
        Log.d(getClass().getSimpleName(), "구글 Code: " + authCode);

        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                // Firebase 로그인 성공
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.getIdToken(false).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            String idToken = task2.getResult().getToken();
                            // 여기에서 새로운 ID 토큰을 처리합니다.
                            Log.d("ID_TOKEN", "ID Token: " + idToken);

                            model = new LoginModel(mAuth.getCurrentUser(), RC_GOOGLE, idToken);
                            LoginRequest request = new LoginRequest(model, IntroActivity.isNotificationGranted(), idToken);

                            LoginResponse response = userApiClient.requestSignIn(request);

                            if (response == null) {
                                errorMessage.postValue("로그인 실패 : 탈퇴한 사용자");
                                loading.postValue(false);
                            } else {

                                userPreferences(request, response);
                                sendLogMessage(request, response);

                                UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();
                                userProfilePreferences(userInfoResponse);
                                showLogMessages(userInfoResponse);

                                loading.postValue(false);
                                loginSuccess.postValue(true);
                            }

                        } else {
                            errorMessage.postValue("Failed to retrieve Firebase ID token");
                            loading.postValue(false);
                        }
                    });
                } else {
                    errorMessage.postValue("Firebase user is null");
                    loading.postValue(false);
                }
            } else {
                errorMessage.postValue("Firebase sign-in failed: " + task.getException().getMessage());
                loading.postValue(false);
            }
        });
    }

    private void handleKakaoSignInResult(OAuthToken token) {
        if (token != null) {
            Log.d(getClass().getName(), token.getIdToken());
            Log.d(getClass().getName(), token.getAccessToken());
            signInKakaoWithToken(token.getAccessToken());
        } else {
            errorMessage.postValue("Kakao sign-in failed: Token is null");
            loading.postValue(false);
        }
    }

    private void signInKakaoWithToken(String token) {
        UserApiClient userApiClient = UserApiClient.getInstance();
        userApiClient.me((user, meError) -> {
            if (meError != null) {
                errorMessage.postValue("Kakao me() failed: " + meError.getMessage());
                loading.postValue(false);
            } else {
                model = new LoginModel(user, RC_KAKAO);
                LoginRequest request = new LoginRequest(model, IntroActivity.isNotificationGranted(), token);
                LoginResponse response = this.userApiClient.requestSignIn(request);

                if (response == null) {
                    errorMessage.postValue("로그인 실패 : 탈퇴한 사용자");
                    loading.postValue(false);
                } else {
                    userPreferences(request, response);
                    sendLogMessage(request, response);

                    UserInfoResponse userInfoResponse = this.userApiClient.requestUserInfo();
                    userProfilePreferences(userInfoResponse);
                    showLogMessages(userInfoResponse);

                    loading.postValue(false);
                    loginSuccess.postValue(true);
                }
            }
            return null;
        });
    }

    public void userProfilePreferences(UserInfoResponse response) {
        SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", response.getName());
        editor.putString("userId", response.getUserId());
        editor.putString("publicFolderId", response.getPublicFolderId());
        editor.putString("userCode", response.getCode());
        editor.apply();
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
        editor.apply();
    }

    public void sendLogMessage(TokenResponse response) {
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();

        Log.d(getClass().getName(), "AT: " + accessToken);
        Log.d(getClass().getName(), "RT: " + refreshToken);
    }

    public void sendLogMessage(LoginRequest request, LoginResponse response) {
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();

        Log.d(getClass().getName(), "AT: " + accessToken);
        Log.d(getClass().getName(), "RT: " + refreshToken);
    }

    public void showLogMessages(UserInfoResponse response) {
        Log.d(getClass().getName(), "프로필 조회 성공");
        ArrayList<String> logs = new ArrayList<>();
        logs.add("userName      :" + response.getName());
        logs.add("userId        :" + response.getUserId());
        logs.add("publicFolderId:" + response.getPublicFolderId());
        logs.add("userCode      :" + response.getCode());

        for (int i = 0; i < logs.toArray().length; i++) {
            Log.d(getClass().getName(), logs.get(i));
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GOOGLE) {
                handleGoogleSignInResult(data);
            }
        }
    }
}