package team.y2k2.globa.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityLoginBinding;
import team.y2k2.globa.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    // 구글
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private final int RC_KAKAO = 1001;
    private final int RC_NAVER = 1002;
    private final int RC_TWITTER = 1003;
    private final int RC_GOOGLE = 1004;

    public int GOOGLE = R.id.button_sign_in_google;
    /*
    public int NAVER = R.id.button_sign_in_naver;
    public int TWITTER = R.id.button_sign_in_twitter;
    public int KAKAO = R.id.button_sign_in_kakao;
     */
    private ActivityLoginBinding binding;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setFirstCharColorPrimary();
        googleServiceLoading();

//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if(!Environment.isExternalStorageManager()){
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(intent);
//            }
//        }

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
        /*
        else if(SIGN_IN_TYPE == TWITTER) {

        }

        else if(SIGN_IN_TYPE == KAKAO) {

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
}