package team.y2k2.globa.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import retrofit2.Response;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;

public class MainModel {
    private final Activity activity;
    private UserApiClient apiClient;

    public MainModel(Activity activity) {
        this.activity = activity;
    }

    public void handleUserFcmToken(MainModelCallback callback) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onTokenFailure(task.getException());
                return;
            }
            String fcmToken = task.getResult();
            String userId = getUserInfo();
            updateToken(userId, fcmToken, callback);
        });
    }

    public String getUserInfo() {
        apiClient = new UserApiClient();
        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        return userInfoResponse.getUserId();
    }

    public void updateToken(String userId, String token, MainModelCallback callback) {
        Response<Void> response = apiClient.updateToken(userId, token);

        if (response.isSuccessful()) {
            callback.onTokenUpdateSuccess();
        }
        else {
            callback.onTokenUpdateFailure(response.code(), response.message());
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