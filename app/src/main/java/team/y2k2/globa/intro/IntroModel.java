package team.y2k2.globa.intro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class IntroModel {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private static boolean isNofiGranted = false;

    private final Context context;

    public IntroModel(Context context) {
        this.context = context;
    }

    public static boolean isNofiGranted() {
        Log.d("알림 수락 여부", "알림 수락 여부 getter 실행: " + isNofiGranted);
        return isNofiGranted;
    }

    public boolean checkAutoLogin() {
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String refreshToken = preferences.getString("refreshToken", "");
        String accessToken = preferences.getString("accessToken", "");
        return !refreshToken.equalsIgnoreCase("") && !accessToken.equalsIgnoreCase("");
    }

    public void requestNotificationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isNofiGranted = true;
                FirebaseMessaging.getInstance().subscribeToTopic("notification");
                FirebaseMessaging.getInstance().subscribeToTopic("notice");
                FirebaseMessaging.getInstance().subscribeToTopic("event");
            } else {
                isNofiGranted = false;
                Toast.makeText(context, "알림 수신이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}