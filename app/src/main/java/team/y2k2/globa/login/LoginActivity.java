package team.y2k2.globa.login;

import static team.y2k2.globa.login.LoginModel.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.kakao.sdk.common.model.AuthErrorCause;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.OAuthLoginCallback;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityLoginBinding;
import team.y2k2.globa.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    // 구글
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    public int GOOGLE = R.id.button_sign_in_google;
    public int KAKAO = R.id.button_sign_in_kakao;

    /*
    public int NAVER = R.id.button_sign_in_naver;
    public int TWITTER = R.id.button_sign_in_twitter;
     */
    private ActivityLoginBinding binding;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        KakaoSdk.init(this, "8a572ba277b6059bd51f23fe58474f13");
        setFirstCharColorPrimary();
        googleServiceLoading();

        setContentView(binding.getRoot());
    }

    /**
     * 첫 글자의 색상을 Primary Color로 지정합니다.
     */
    private void setFirstCharColorPrimary() {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewLoginTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewLoginTitle.setText(spanTitle);
    }

    /**
     * SNS 로그인 종류에 따라 실행될 메서드를 지정합니다.
     */
    public void onSignInClick(View v) {
        final int SIGN_IN_TYPE = v.getId();

        if(SIGN_IN_TYPE == GOOGLE) {
            Log.d(getClass().getName(), "버튼 클릭");

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1004);
        }
        else if(SIGN_IN_TYPE == KAKAO) {
            Log.d(getClass().getName(), "카카오 클릭");
            String keyHash = Utility.INSTANCE.getKeyHash(this);
            Log.d("KeyHash", keyHash);

            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, kakaoLoginCallback);

            } else {
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, kakaoLoginCallback);
            }
        }
        /*
        else if(SIGN_IN_TYPE == TWITTER) {

        }



        else if(SIGN_IN_TYPE == NAVER) {

        }
         */
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
     * @NAVER = 1002
     * @TWITTER = 1003
     * @GOOGLE = 1004
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getName(), "Result 시작");
        super.onActivityResult(requestCode, resultCode, data);

//        if(resultCode == RESULT_CANCELED) {
//            Log.d("LoginActivityLog", "resultCode: CANCELED");
//            return;
//        }
        switch (requestCode) {
            case RC_KAKAO:
                Log.d(getClass().getName(), "onActivityResult 카카오 로그인");

                break;
            case RC_NAVER:
            case RC_TWITTER:
            case RC_GOOGLE:
                signInGoogle(data);
                break;
        }
        Log.d(getClass().getName(), "Result 종료");

    }

    private void signInGoogle(Intent data) {
        Log.d(getClass().getName(), "로그인 시작");
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuth(account);
        } catch (ApiException e) {
            // 로그인 실패
            Toast.makeText(getApplicationContext(), "로그인 실패 :" + e.getMessage() , Toast.LENGTH_SHORT).show();
            Log.d(getClass().getName(), "로그인 실패 : " + e.getMessage());
        }
        Log.d(getClass().getName(), "로그인 종료");
    }

    private void firebaseAuth(GoogleSignInAccount acct) {
        String accessToken = acct.getIdToken();

        if (accessToken == null) {
            Toast.makeText(getApplicationContext(), "로그인 실패 : NULL of AccessToken", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new LoginViewModel.LoginListener(this, mAuth, accessToken));
    }

    private void googleServiceLoading() {
        //Google의 로그인 구성을 설정해준다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    // 카카오 로그인 콜백 함수
    Function2<OAuthToken, Throwable, Unit> kakaoLoginCallback = (token, error) -> {
        if (error != null) {
            Log.e(getClass().getName(), "로그인 실패", error);
        } else if (token != null) {
            Log.i(getClass().getName(), "로그인 성공(토큰) : " + token.getAccessToken());
            Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
            // 사용자 정보 요청
            requestMe(); // 사용자 정보 요청 함수 호출
        }
        return null;
    };

    // 사용자 정보 요청 함수
    private void requestMe() {
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(getClass().getName(), "사용자 정보 요청 실패", meError);
            } else {
                Log.i(getClass().getName(),
                        "사용자 정보 요청 성공" +
                        "\n회원번호: " + user.getId() +
                        "\n닉네임: " + user.getKakaoAccount().getProfile().getNickname() +
                        "\n프로필: " + user.getKakaoAccount().getProfile().getProfileImageUrl() );
                // 사용자 정보를 활용하여 필요한 작업 수행 (예: 메인 화면으로 이동)

                LoginViewModel.LoginListener listener = new LoginViewModel.LoginListener(user.getId().toString(), user.getKakaoAccount().getProfile().getNickname(), user.getKakaoAccount().getProfile().getProfileImageUrl(), this);
                listener.KakaoLogin();
            }
            return null;
        });
    }


    OAuthLoginCallback oauthLoginCallback = new OAuthLoginCallback() {
        @Override
        public void onSuccess() {
            // 로그인 성공
            String accessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
            // 사용자 정보 가져오기 등 추가 작업 수행
        }

        @Override
        public void onFailure(int httpStatus, String message) {
            // 로그인 실패
            // 오류 처리
        }

        @Override
        public void onError(int errorCode, String message) {
            // 로그인 에러
            // 오류 처리
        }
    };
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 로그인 화면 종료
    }
}