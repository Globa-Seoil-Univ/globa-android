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
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import team.y2k2.globa.R;
import team.y2k2.globa.login.LoginModel;

public class SnsLoginManager {
    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final GoogleSignInClient mGoogleSignInClient;
    private final Function2<OAuthToken, Throwable, Unit> signInKakaoCallback;
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
        this.signInKakaoCallback = createKakaoCallback();
    }

    private GoogleSignInClient initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestServerAuthCode(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    private Function2<OAuthToken, Throwable, Unit> createKakaoCallback() {
        return (token, error) -> {
            if (error != null) {
                callback.onError("Kakao sign-in failed: " + error.getMessage());
            } else if (token != null) {
                handleKakaoSignInResult(token);
            }
            return null;
        };
    }

    public void startSignIn(int signInType) {
        if (signInType == LoginModel.RC_GOOGLE) {
            signInGoogle();
        } else if (signInType == LoginModel.RC_KAKAO) {
            signInKakao();
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, LoginModel.RC_GOOGLE);
    }

    private void signInKakao() {
        UserApiClient userApiClient = UserApiClient.getInstance();
        if (userApiClient.isKakaoTalkLoginAvailable(activity)) {
            userApiClient.loginWithKakaoTalk(activity, signInKakaoCallback);
        } else {
            userApiClient.loginWithKakaoAccount(activity, signInKakaoCallback);
        }
    }

    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            signInGoogleWithFirebase(account);
        } catch (ApiException e) {
            callback.onError("Google sign-in failed: " + e.getMessage());
        }
    }

    private void signInGoogleWithFirebase(GoogleSignInAccount acct) {
        String accessToken = acct.getIdToken();
        String authCode = acct.getServerAuthCode();

        if (accessToken == null) {
            callback.onError("Google Access Token is null");
            return;
        }

        Log.d(getClass().getSimpleName(), "구글 AT: " + accessToken);
        Log.d(getClass().getSimpleName(), "구글 Code: " + authCode);

        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.getIdToken(false).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            String idToken = task2.getResult().getToken();
                            LoginModel model = new LoginModel(user, LoginModel.RC_GOOGLE, idToken);
                            callback.onSuccess(model, idToken);
                        } else {
                            callback.onError("Failed to retrieve Firebase ID token");
                        }
                    });
                } else {
                    callback.onError("Firebase user is null");
                }
            } else {
                callback.onError("Firebase sign-in failed: " + task.getException().getMessage());
            }
        });
    }

    private void handleKakaoSignInResult(OAuthToken token) {
        if (token != null) {
            Log.d(getClass().getName(), token.getIdToken());
            Log.d(getClass().getName(), token.getAccessToken());
            signInKakaoWithToken(token.getAccessToken());
        } else {
            callback.onError("Kakao sign-in failed: Token is null");
        }
    }

    private void signInKakaoWithToken(String token) {
        UserApiClient userApiClient = UserApiClient.getInstance();
        userApiClient.me((user, meError) -> {
            if (meError != null) {
                callback.onError("Kakao me() failed: " + meError.getMessage());
            } else {
                LoginModel model = new LoginModel(user, LoginModel.RC_KAKAO);
                callback.onSuccess(model, token);
            }
            return null;
        });
    }
} 