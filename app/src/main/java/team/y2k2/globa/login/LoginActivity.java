package team.y2k2.globa.login;

import static team.y2k2.globa.login.LoginModel.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    public int GOOGLE = R.id.button_sign_in_google;
    public int KAKAO = R.id.button_sign_in_kakao;

    public final String LOGIN_ERR_MSG = "로그인 오류가 발생했습니다." ;

    private ActivityLoginBinding binding;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setFirstCharColorPrimary();

        initKakaoSdk();
        initGoogleSdk();
    }

    /**
     * LGN-1. SNS 로그인 종류에 따라 실행될 메서드를 지정합니다.
     */
    public void onSignInClick(View v) {
        final int SIGN_IN_TYPE = v.getId();

        if(SIGN_IN_TYPE == GOOGLE) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE);
        }
        else if(SIGN_IN_TYPE == KAKAO) {

            UserApiClient userApiClient = UserApiClient.getInstance();
            // 카카오톡 설치 여부 확인
            if (userApiClient.isKakaoTalkLoginAvailable(this)) {
                userApiClient.loginWithKakaoTalk(this, signInKakaoOfCallback);
            } else {
                userApiClient.loginWithKakaoAccount(this, signInKakaoOfCallback);
            }
        }
    }

    /**
     * 첫 글자의 색상을 Primary Color로 지정합니다.
     */
    private void setFirstCharColorPrimary() {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewLoginTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewLoginTitle.setText(spanTitle);
    }

    /***
     * LGN-2. SNS - 카카오 로그인 인증 정보를 가져옵니다.
     */
    private void initKakaoSdk() {
        KakaoSdk.init(this, viewModel.getAppKeyForKakao());
    }

    private void initGoogleSdk() {
        //Google의 로그인 구성을 설정해준다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * SNS 계정 선택 후 해당 엑티비티로 돌아왔을 때 결과값을 가져옵니다.
     * 가져온 SNS 계정 정보와 requestCode를 구분해 Spring에 정보를 전달합니다.

     *
     * @param requestCode SNS Type
     * @param resultCode SNS 계정 정보 요청 성공 여부
     * @param data SNS 계정 정보
     *
     * @KAKAO = 1001
     * @GOOGLE = 1004
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_KAKAO:
                break;
            case RC_NAVER:
            case RC_TWITTER:
            case RC_GOOGLE:
                signInGoogle(data);
                break;
        }
    }

    private void signInGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            signInGoogleOfFirebaseAuth(account);
        }
        catch (ApiException e) {
            Toast.makeText(this, LOGIN_ERR_MSG + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void signInGoogleOfFirebaseAuth(GoogleSignInAccount acct) {
        String accessToken = acct.getIdToken();

        if (accessToken == null) {
            String e = "액세스 토큰 없음";
            Toast.makeText(this,  LOGIN_ERR_MSG + e, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new LoginViewModel.LoginListener(this, mAuth, accessToken));
    }

    private void signInKakao() {
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Toast.makeText(this, LOGIN_ERR_MSG + meError, Toast.LENGTH_LONG);
            } else {
                LoginViewModel.LoginListener listener = new LoginViewModel.LoginListener(user, this);
                listener.KakaoLogin();
            }
            return null;
        });
    }

    Function2<OAuthToken, Throwable, Unit> signInKakaoOfCallback = (token, error) -> {
        if (error != null) {
            Toast.makeText(this, LOGIN_ERR_MSG + error, Toast.LENGTH_LONG);
        }
        else if (token != null) {
            signInKakao();
        }
        return null;
    };
}