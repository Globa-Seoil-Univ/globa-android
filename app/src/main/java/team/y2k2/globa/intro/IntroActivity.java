package team.y2k2.globa.intro;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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

        binding.setViewModel(viewModel); // ViewModel 바인딩
        binding.setLifecycleOwner(this); // LiveData 관찰을 위해 LifecycleOwner 설정

        setFirstCharColorPrimary(binding.textviewIntroLogo);

        viewModel.autoLogin();
        viewModel.getAutoLoginSuccess().observe(this, success -> {
            if (success) {
                viewModel.navigateToMain(this);
            }
        });

        viewModel.requestNotificationPermission(this);
        viewModel.getNotificationPermissionGranted().observe(this, granted -> {
        });
    }

    public void setFirstCharColorPrimary(TextView textView) {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewIntroLogo.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanTitle);
    }
}