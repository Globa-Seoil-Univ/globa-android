package team.y2k2.globa.intro;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityIntroBinding;
import team.y2k2.globa.login.LoginActivity;
import team.y2k2.globa.main.MainActivity;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoLogin();
        binding = ActivityIntroBinding.inflate(getLayoutInflater());

        setFirstCharColorPrimary(binding.textviewIntroLogo);
        binding.buttonIntroBottomStart.setOnClickListener(view -> loginView());

        setContentView(binding.getRoot());
    }

    public void setFirstCharColorPrimary(TextView textView) {
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewIntroLogo.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spanTitle);
    }

    protected void autoLogin() {
        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String refreshToken = preferences.getString("refreshToken", "");
        String accessToken = preferences.getString("accessToken", "");

        if(! refreshToken.equalsIgnoreCase("") && ! accessToken.equalsIgnoreCase("")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }
    }

    protected void loginView() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}