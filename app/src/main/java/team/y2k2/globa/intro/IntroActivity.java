package team.y2k2.globa.intro;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityIntroBinding;
import team.y2k2.globa.login.LoginActivity;
import team.y2k2.globa.main.MainActivity;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private static boolean isNofiGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoLogin();
        binding = ActivityIntroBinding.inflate(getLayoutInflater());

        setFirstCharColorPrimary(binding.textviewIntroLogo);
        binding.buttonIntroBottomStart.setOnClickListener(view -> loginView());

        requestNotificationPermission();

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

    private void requestNotificationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                // 알림을 보내는 기능을 구현
                Log.d("알림 수락 여부", "알림 수신 수락됨");
                isNofiGranted = true;
            } else {
                // 권한이 거부된 경우
                Log.d("알림 수락 여부", "알림 수신 거부됨");
                isNofiGranted = false;
                Toast.makeText(this, "알림 수신이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static boolean isNofiGranted() {
        Log.d("알림 수락 여부", "알림 수락 여부 getter 실행: " + isNofiGranted);
        return isNofiGranted;
    }
}