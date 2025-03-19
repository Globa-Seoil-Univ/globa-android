package team.y2k2.globa.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

import team.y2k2.globa.R;
import team.y2k2.globa.intro.IntroActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String CHANNEL_ID = "firebase_channel";
    private static final int NOTIFICATION_ID = 1001;

    public FirebaseMessagingService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCM 토큰", "새로운 토큰 발급 : onNewToken() 실행");
        Log.d("FCM 토큰", "FCM 토큰 : " + token);

        // FCM 토큰 프리퍼런스에 저장
        SharedPreferences fcmPref = getSharedPreferences("fcm_token", MODE_PRIVATE);
        SharedPreferences.Editor fcmPrefEditor = fcmPref.edit();
        fcmPrefEditor.putString("fcm_token", token);
        fcmPrefEditor.apply();

    }

    // 메시지가 오지 않는 경우도 존재함.
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(getClass().getSimpleName(), "알림 리시버 시작");

        String title = null;
        String message = null;

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            title = notification.getTitle();
            message = notification.getBody();
        } else {
            // 데이터 메시지 처리 또는 기본값 설정
            Log.d(getClass().getSimpleName(), "데이터 메시지 수신");
            if (remoteMessage.getData().containsKey("title")) {
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("body");
            }
        }

        if (title != null && message != null) {
            sendNotification(title, message);
        } else {
            Log.w(getClass().getSimpleName(), "알림 또는 데이터 메시지에서 title 또는 body가 누락됨.");
        }
    }

    private void sendNotification(String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // 알림 채널 생성
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Firebase Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Default Description");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // 알림을 클릭했을 때 열릴 인텐트
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // 알림 빌더 설정
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(message).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent);

        // 알림을 시스템에 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }
}
