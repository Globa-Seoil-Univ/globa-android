package team.y2k2.globa.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityLoginBinding;
import team.y2k2.globa.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    // 구글
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="mainTag";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN=123;

    // 네이버

    //
    private ActivityLoginBinding binding;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewLoginTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewLoginTitle.setText(spanTitle);

        googleServiceLoading();

        setContentView(binding.getRoot());

        binding.buttonSignInNaver.setOnClickListener(view -> signInWithNaver());
        binding.buttonSignInGoogle.setOnClickListener(view -> signInWithGoogle());
        binding.buttonSignInKakao.setOnClickListener(view -> signInWithKakao());
        binding.buttonSignInTwitter.setOnClickListener(view -> signInWithTwitter());
    }

    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signInWithNaver() {
    }

    public void signInWithKakao() {
    }

    public void signInWithTwitter() {
    }


    @Override
    public void onStart() {
        super.onStart();
        //활동을 초기화할때 최근에 사용된 계정이 있는지 확인한다.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // 구글 로그인 실패 처리
                Toast.makeText(getApplicationContext(), "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                Log.d("ERRLogin", e.toString());
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // 구글 로그인에서 받은 idToken을 사용하여 Firebase 인증 정보를 생성
        String idToken = acct.getIdToken();
        if (idToken != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Firebase 로그인 성공
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                userPreferences(idToken, uid);

                                Toast.makeText(getApplicationContext(), "Logged in as " + uid, Toast.LENGTH_SHORT).show();
                                // 여기에 선택된 계정 정보를 다음 화면으로 넘기는 코드를 추가하세요.
                                // 예: Intent를 사용하여 다음 화면으로 넘기기
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Firebase 로그인 실패
                                Toast.makeText(getApplicationContext(), "Firebase Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // idToken이 null인 경우 처리
            Toast.makeText(getApplicationContext(), "Google Sign-In Failed: idToken is null", Toast.LENGTH_SHORT).show();
            Log.d("ERRLogin", "idToken is null");
        }
    }

    public void userPreferences(String accessToken, String uid) {
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accessToken", accessToken);
        editor.putString("uid", uid);
        editor.commit();
    }

    public void googleServiceLoading() {
        //Google의 로그인 구성을 설정해준다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }
}