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
    private static final String TAG = "IntroModel";

    private final Context context;

    public IntroModel(Context context) {
        this.context = context;
    }

    public static boolean isNofiGranted() {
        Log.d(TAG, "isNofiGranted() 호출됨. 현재 값: " + isNofiGranted);
        return isNofiGranted;
    }

    public boolean hasRefreshToken() {
        Log.d(TAG, "hasRefreshToken: 자동 로그인 가능 여부 확인 시작...");
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String refreshToken = preferences.getString("refreshToken", "");
        boolean hasToken = !refreshToken.isEmpty();
        Log.d(TAG, "hasRefreshToken: 저장된 Refresh Token: '" + refreshToken + "', 존재 여부: " + hasToken);
        return hasToken;
    }

    public String getRefreshToken() {
        Log.d(TAG, "getRefreshToken: 저장된 Refresh Token 읽기 시도...");
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String refreshToken = preferences.getString("refreshToken", null);
        Log.d(TAG, "getRefreshToken: 읽어온 Refresh Token: '" + refreshToken + "'");
        return refreshToken;
    }

    public void requestNotificationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d(TAG, "알림 권한을 요청합니다.");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        } else {
            Log.d(TAG, "이미 알림 권한이 허용되어 있습니다.");
        }
    }

    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "알림 권한이 허용되었습니다.");
                isNofiGranted = true;
                FirebaseMessaging.getInstance().subscribeToTopic("notification");
                FirebaseMessaging.getInstance().subscribeToTopic("notice");
                FirebaseMessaging.getInstance().subscribeToTopic("event");
            } else {
                Log.w(TAG, "알림 권한이 거부되었습니다.");
                isNofiGranted = false;
                Toast.makeText(context, "알림 수신이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

