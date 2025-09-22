package team.y2k2.globa.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import retrofit2.Response;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class MainModel {
    private final Activity activity;
    private UserApiClient apiClient;
    public List<Record> records;

    public MainModel(Activity activity) {
        this.activity = activity;
        this.apiClient = new UserApiClient(activity);
    }
    public MainModel(List<Record> records) {
        this.activity = null; // 이 경우 activity가 필요 없으므로 null로 초기화
        this.records = records;
    }

    public void handleUserFcmToken(MainModelCallback callback) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onTokenFailure(task.getException());
                return;
            }
            String fcmToken = task.getResult();
            String userId = getUserInfo();
            if (userId != null) {
                updateToken(fcmToken, callback);
            } else {
                Log.e(getClass().getName(), "사용자 ID가 없어 FCM 토큰을 업데이트할 수 없습니다.");
            }
        });
    }

    public String getUserInfo() {
        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        if (userInfoResponse == null) {
            Log.e(getClass().getName(), "사용자 정보 조회 API가 null을 반환했습니다.");
            return null;
        }
        return userInfoResponse.getUserId();
    }

    public void updateToken(String token, MainModelCallback callback) {
        Response<?> response = apiClient.updateToken(token);

        if (response != null && response.isSuccessful()) {
            callback.onTokenUpdateSuccess();
        }
        else {
            int code = response != null ? response.code() : -1;
            String message = response != null ? response.message() : "Response is null";
            callback.onTokenUpdateFailure(code, message);
        }
    }

    public String getUserAccessToken() {
        SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
        return preferences.getString("accessToken", "");
    }

    public void uploadRecord(Intent data, MainModelCallback callback) {
        if (data != null && data.getData() != null) {
            Uri audioUri = data.getData();
            String audioPath = getRealPathFromURI(audioUri);
            String audioName = getFileNameFromURI(audioUri);

            callback.onRecordUploadReady(audioPath, audioName);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("downloadedFile", ".tmp", activity.getCacheDir());

            try (OutputStream outputStream = Files.newOutputStream(tempFile.toPath())) {
                byte[] buffer = new byte[1024];
                int length;
                if (inputStream != null) {
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private String getFileNameFromURI(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return uri.getLastPathSegment();
    }

    public interface MainModelCallback {
        void onTokenUpdateSuccess();

        void onTokenUpdateFailure(int code, String message);

        void onTokenUpdateFailure(Throwable t);

        void onTokenFailure(Exception e);

        void onRecordUploadReady(String audioPath, String audioName);
    }
}
