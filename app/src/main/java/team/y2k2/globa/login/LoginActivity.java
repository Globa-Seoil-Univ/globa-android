package team.y2k2.globa.login;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.common.KakaoSdk;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityLoginBinding;
import team.y2k2.globa.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_ERR_MSG = "로그인 오류가 발생했습니다.";
    private ActivityLoginBinding binding;
    private LoginActivityModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getBooleanExtra("expired", false)) {
            Toast.makeText(this, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
        }

        setupViewModel();
        setupUI();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginActivityModel.class);
        viewModel.setContext(this);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    private void setupUI() {
        setFirstCharColorPrimary();
        initKakaoSdk();
        initGoogleSdk();
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoadingDialog();
            } else {
                dismissLoadingDialog();
            }
        });

        viewModel.getLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                FirebaseMessaging.getInstance().subscribeToTopic("NOTICE");
                FirebaseMessaging.getInstance().subscribeToTopic("EVENT");

                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, LOGIN_ERR_MSG + ": " + errorMessage, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    private void setFirstCharColorPrimary() {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewLoginTitle.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewLoginTitle.setText(spanTitle);
    }

    private void initKakaoSdk() {
        KakaoSdk.init(this, getString(R.string.kakao_app_key));
    }

    private void initGoogleSdk() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(this, gso);
        FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.handleActivityResult(requestCode, resultCode, data);
    }

    private void showLoadingDialog() {
        // 로딩 다이얼로그 표시 로직
    }

    private void dismissLoadingDialog() {
        // 로딩 다이얼로그 숨김 로직
    }

    public LoginActivityModel getViewModel() {
        return viewModel;
    }
}