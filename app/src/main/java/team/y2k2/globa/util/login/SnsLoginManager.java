package team.y2k2.globa.util.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

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
import com.kakao.sdk.user.UserApiClient;

import team.y2k2.globa.R;
import team.y2k2.globa.login.LoginModel;

public class SnsLoginManager {
    private static final String TAG = "SnsLoginManager";
    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final GoogleSignInClient mGoogleSignInClient;
    private SnsLoginCallback callback;

    public interface SnsLoginCallback {
        void onSuccess(LoginModel loginModel, String token);
        void onError(String errorMessage);
    }

    public SnsLoginManager(Activity activity, SnsLoginCallback callback) {
        this.activity = activity;
        this.callback = callback;
        this.mAuth = FirebaseAuth.getInstance();
        this.mGoogleSignInClient = initGoogleSignInClient();
        Log.d(TAG, "SnsLoginManager가 초기화되었습니다.");
    }

    private GoogleSignInClient initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestServerAuthCode(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    public void startSignIn(int signInType) {
        Log.d(TAG, "startSignIn 호출됨. Type: " + signInType);
        if (signInType == LoginModel.RC_GOOGLE) {
            signInGoogle();
        } else if (signInType == LoginModel.RC_KAKAO) {
            signInKakao();
        }
    }

    private void signInGoogle() {
        Log.d(TAG, "구글 로그인을 시작합니다.");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, LoginModel.RC_GOOGLE);
    }

    private void signInKakao() {
        Log.d(TAG, "카카오 로그인을 시작합니다.");

        KakaoAuthManager.setCallback(new SnsLoginCallback() {
            @Override
            public void onSuccess(LoginModel loginModel, String token) {
                signInKakaoWithToken(token);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });

        UserApiClient userApiClient = UserApiClient.getInstance();
        if (userApiClient.isKakaoTalkLoginAvailable(activity)) {
            Log.d(TAG, "카카오톡으로 로그인을 시도합니다.");
            userApiClient.loginWithKakaoTalk(activity, KakaoAuthManager.kakaoLoginCallback);
        } else {
            Log.d(TAG, "카카오계정으로 로그인을 시도합니다.");
            userApiClient.loginWithKakaoAccount(activity, KakaoAuthManager.kakaoLoginCallback);
        }
    }

    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.i(TAG, "구글 로그인 성공. Firebase로 인증을 시도합니다. User: " + account.getEmail());
            signInGoogleWithFirebase(account);
        } catch (ApiException e) {
            Log.e(TAG, "구글 로그인 실패: " + e.getMessage(), e);
            callback.onError("Google sign-in failed: " + e.getMessage());
        }
    }

    private void signInGoogleWithFirebase(GoogleSignInAccount acct) {
        String accessToken = acct.getIdToken();
        if (accessToken == null) {
            Log.e(TAG, "Google Access Token is null");
            callback.onError("Google Access Token is null");
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG, "Firebase 인증 성공. User: " + user.getUid());
                    user.getIdToken(false).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            String idToken = task2.getResult().getToken();
                            LoginModel model = new LoginModel(user, LoginModel.RC_GOOGLE, idToken);
                            Log.d(TAG, "ViewModel으로 onSuccess 콜백을 전달합니다.");
                            callback.onSuccess(model, idToken);
                        } else {
                            callback.onError("Failed to retrieve Firebase ID token");
                        }
                    });
                } else {
                    callback.onError("Firebase user is null");
                }
            } else {
                Log.e(TAG, "Firebase 인증 실패: " + task.getException().getMessage(), task.getException());
                callback.onError("Firebase sign-in failed: " + task.getException().getMessage());
            }
        });
    }

    private void signInKakaoWithToken(String token) {
        Log.d(TAG, "signInKakaoWithToken: AccessToken=" + token);
        UserApiClient userApiClient = UserApiClient.getInstance();
        Log.d(TAG, "카카오 사용자 정보(me API)를 요청합니다.");
        userApiClient.me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "카카오 me() API 호출 실패: " + meError.getMessage(), meError);
                callback.onError("Kakao me() failed: " + meError.getMessage());
            } else {
                Log.i(TAG, "카카오 me() API 호출 성공. User ID: " + user.getId());
                LoginModel model = new LoginModel(user, LoginModel.RC_KAKAO);
                Log.d(TAG, "ViewModel으로 onSuccess 콜백을 전달합니다.");
                callback.onSuccess(model, token);
            }
            return null;
        });
    }
}