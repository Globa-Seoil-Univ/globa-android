package team.y2k2.globa.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.databinding.ActivityLoginBinding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.common.KakaoSdk;

import team.y2k2.globa.R;
import team.y2k2.globa.main.MainActivity;

import android.widget.Toast;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {
    public final String LOGIN_ERR_MSG = "로그인 오류가 발생했습니다.";
    public AlertDialog dialog;
    AlertDialog.Builder builder;
    private ActivityLoginBinding binding;
    private LoginActivityModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getBooleanExtra("expired", false))
            Toast.makeText(this, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();

        viewModel = new ViewModelProvider(this).get(LoginActivityModel.class);
        viewModel.setContext(this);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);

        builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setFirstCharColorPrimary();
        initKakaoSdk();
        initGoogleSdk();

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
        });

        viewModel.getLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, LOGIN_ERR_MSG + ": " + errorMessage, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage(); // Clear the message after displaying it
            }
        });
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
        Log.d("KAKAO_KEY", KakaoSdk.INSTANCE.getKeyHash());
    }

    private void initGoogleSdk() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(this.getString(R.string.default_web_client_id)).requestServerAuthCode(this.getString(R.string.default_web_client_id)).requestEmail().build();

        GoogleSignIn.getClient(this, gso);
        FirebaseAuth.getInstance();
    }


    /**
     * SNS 계정 선택 후 해당 엑티비티로 돌아왔을 때 결과값을 가져옵니다.
     * 가져온 SNS 계정 정보와 requestCode를 구분해 Spring에 정보를 전달합니다.
     *
     * @param requestCode SNS Type
     * @param resultCode  SNS 계정 정보 요청 성공 여부
     * @param data        SNS 계정 정보
     *                    KAKAO = 1001
     *                    GOOGLE = 1004
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.handleActivityResult(requestCode, resultCode, data);
    }
}