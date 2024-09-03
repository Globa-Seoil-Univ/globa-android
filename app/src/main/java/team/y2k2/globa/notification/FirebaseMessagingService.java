package team.y2k2.globa.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.messaging.RemoteMessage;

import team.y2k2.globa.R;
import team.y2k2.globa.intro.IntroActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String CHANNEL_ID = "firebase_channel";

    private NotificationViewModel notificationViewModel;
    private SharedPreferences tokenPreferences = getSharedPreferences("notificationToken", MODE_PRIVATE);
    private SharedPreferences.Editor tokenEditor = tokenPreferences.edit();

    public FirebaseMessagingService() {
        this.notificationViewModel = new ViewModelProvider((ViewModelStoreOwner) getApplicationContext()).get(NotificationViewModel.class);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // 새로운 토큰 호스트로 전송
        String storedToken = tokenPreferences.getString("fcm_token", null);
        if(storedToken == null) {
            tokenEditor.putString("fcm_token", token);
            tokenEditor.apply();
            notificationViewModel.registerFirstToken(token);
        } else {
            tokenEditor.putString("fcm_token", token);
            tokenEditor.apply();
            notificationViewModel.updateToken(token);
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(!remoteMessage.getData().isEmpty()) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            sendNotification(title, message);
        }

    }

    private void sendNotification(String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // 알림 채널 생성
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Firebase Notification", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // 알림을 클릭했을 때 열릴 인텐트
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // 알림 빌더 설정
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 알림을 시스템에 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());

    }
}
