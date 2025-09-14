package team.y2k2.globa.intro;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;
    IntroActivityModel viewModel;
    ApiClient apiClient;

    public static boolean isNotificationGranted() {
        return IntroActivityModel.isNofiGranted();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        viewModel = new ViewModelProvider(this).get(IntroActivityModel.class);
        viewModel.setContext(this);

        apiClient = ApiClient.getInstance(this);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setFirstCharColorPrimary(binding.textviewIntroLogo);

        checkServerAndProceed();
    }

    private void checkServerAndProceed() {
        apiClient.isServerOpened()
                .thenAccept(isOpened -> new Handler(Looper.getMainLooper()).post(() -> {
                    if (isOpened) {
                        viewModel.autoLogin();
                        viewModel.getAutoLoginSuccess().observe(this, success -> {
                            if (success) {
                                Log.d(getClass().getName(), "로그인 성공");
                                viewModel.navigateToMain(this);
                            } else {
                                binding.buttonIntroBottomStart.setVisibility(View.VISIBLE);
                            }
                        });
                        viewModel.requestNotificationPermission(this);
                        viewModel.getNotificationPermissionGranted().observe(this, granted -> {
                        });

                    } else {
                        showServerUnreachableDialog();
                    }
                }))
                .exceptionally(throwable -> {
                    new Handler(Looper.getMainLooper()).post(this::showServerUnreachableDialog);
                    return null;
                });
    }

    private void showServerUnreachableDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_server_unavailable_title)
                .setMessage("서버에 연결할 수 없습니다.")
                .setPositiveButton(R.string.confirm, (dialog, which) -> finishAffinity())
                .setCancelable(false)
                .show();
    }

    public void setFirstCharColorPrimary(TextView textView) {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewIntroLogo.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanTitle);
    }

    public IntroActivityModel getViewModel() {
        return viewModel;
    }
}