package team.y2k2.globa.main;

import static team.y2k2.globa.api.ApiClient.apiService;
import static team.y2k2.globa.api.ApiClient.authorization;
import static team.y2k2.globa.main.MainModel.PRF_RECORD_NAME;
import static team.y2k2.globa.main.MainModel.PRF_RECORD_PATH;
import static team.y2k2.globa.main.MainModel.REQUEST_CODE_PICK_RECORD;
import static team.y2k2.globa.main.MainModel.REQUEST_CODE_UPLOAD_RECORD;
import static team.y2k2.globa.main.MainModel.TYPE_AUDIO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.NotificationTokenRequest;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.docs.upload.DocsUploadActivity;
import team.y2k2.globa.main.folder.FolderFragment;
import team.y2k2.globa.main.main.MainFragment;
import team.y2k2.globa.main.profile.ProfileFragment;
import team.y2k2.globa.main.statistics.StatisticsFragment;

public class MainViewModel extends ViewModel {
    MainActivity activity;
    MainModel model;
    MainFragment mainFragment;
    StatisticsFragment statisticsFragment;
    ProfileFragment profileFragment;
    FolderFragment folderFragment;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public MainViewModel() {
        mainFragment = new MainFragment();
        statisticsFragment = new StatisticsFragment();
        profileFragment = new ProfileFragment();
        folderFragment = new FolderFragment();

        model = new MainModel();
    }

    public void viewFragment(int index) {
        if(index == R.id.item_main_main)
            replaceFragment(mainFragment);
        else if(index == R.id.item_main_statistics)
            replaceFragment(statisticsFragment);
        else if(index == R.id.item_main_upload)
            uploadAudio();
        else if(index == R.id.item_main_profile)
            replaceFragment(profileFragment);
        else if(index == R.id.item_main_folder)
            replaceFragment(folderFragment);
    }

    private void uploadAudio() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity.getApplication().getApplicationContext());
        bottomSheetDialog.setContentView(R.layout.dialog_upload);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(TYPE_AUDIO);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_RECORD);
    }

    public void uploadRecord(Intent data) {
        if (data != null && data.getData() != null) {
            Uri audioUri = data.getData();
            String audioPath = getRealPathFromURI(audioUri);
            String audioName = getFileNameFromURI(audioUri);

            Intent intent = new Intent(activity, DocsUploadActivity.class);
            intent.putExtra(PRF_RECORD_PATH, audioPath);
            intent.putExtra(PRF_RECORD_NAME, audioName);
            activity.startActivityForResult(intent, REQUEST_CODE_UPLOAD_RECORD);
        }
    }

    private void replaceFragment(Fragment fragment) {
        androidx.fragment.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcv_main, fragment, null)
                .commit();
    }

    private String getRealPathFromURI(Uri uri) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("downloadedFile", ".tmp", activity.getCacheDir());

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("Error : ", e.getMessage());
        }

        return null;
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

    public void getUserIdUpdateToken() {
//        SharedPreferences fcmPref = activity.getSharedPreferences("fcm_token", activity.MODE_PRIVATE);
//        String fcmToken = fcmPref.getString("fcm_token", null);
//        Log.d("FCM 토큰", "FCM 토큰: " + fcmToken);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String fcmToken = task.getResult();

                Log.d("FCM 토큰", "메인 액티비티 FCM 토큰: " + fcmToken);

                String userId = getUserInfo();
                updateToken(userId, fcmToken);
            }
        });

    }

    public String getUserInfo() {
        ApiClient apiClient = new ApiClient(activity);
        UserInfoResponse userInfoResponse = apiClient.requestUserInfo();
        return userInfoResponse.getUserId();
    }

    public void updateToken(String userId, String token) {
        NotificationTokenRequest tokenRequest = new NotificationTokenRequest(token);
        apiService.updateToken(userId, "application/json", authorization, tokenRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("알림 토큰", "알림 토큰 업데이트 완료");
                } else {
                    Log.d("알림 토큰", "알림 토큰 업데이트 실패 : " + response.code() + ", errorMessage: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("알림 토큰", "알림 토큰 업데이트 요청 실패 : " + t.getMessage());
            }
        });
    }

}
