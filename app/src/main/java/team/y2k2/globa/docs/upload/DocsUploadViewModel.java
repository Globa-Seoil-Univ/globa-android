package team.y2k2.globa.docs.upload;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModel;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.Instant;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsUploadViewModel extends ViewModel {
    DocsUploadModel model;
    DocsUploadActivity activity;
    private StorageReference storageReference;
    String userId;
    String folderId;
    DocsUploadFolderAdapter adapter;
    String firebasePath;
    long unixTime;

    FolderResponse response;

    private HandlerThread handlerThread;
    private Handler handler;


    public void setActivity(DocsUploadActivity activity) {
        this.activity = activity;
        this.model = new DocsUploadModel(activity.getIntent());
    }

    private void requestCreateRecord(String title) {
        // 네트워크 요청 보내기
        String path = "folders/" + folderId + "/" + unixTime + ".ogg";
        String folderId = Integer.toString(Integer.parseInt(response.getFolders().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition()).getFolderId()));

        ApiClient apiClient = new ApiClient(activity);

        apiClient.requestCreateRecord(folderId, title, path, "0");
        activity.finish();
    }

    public void loadFolder() {
        ApiClient apiClient = new ApiClient(activity);
        response = apiClient.requestGetFolders(1, 100);

        if (response != null) {
            adapter = new DocsUploadFolderAdapter(activity, R.layout.item_folder, response.getFolders());
            adapter.setDropDownViewResource(R.layout.item_folder);
            activity.binding.spinnerDocsUpload.setAdapter(adapter);
            activity.binding.spinnerDocsUpload.setSelection(0);
        }
    }

    public void uploadRecordFile(String oggPath, String folderId) {
        this.folderId = folderId;
        firebasePath = folderId + "/" + unixTime + ".ogg";
        StorageReference audioRef = storageReference.child(firebasePath);

        Log.d(getClass().getName(), "firebase path : " + firebasePath);

        Uri uri = Uri.fromFile(new File(oggPath));

        audioRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 업로드 성공 시
                    Toast.makeText(activity, "파일 업로드 성공", Toast.LENGTH_SHORT).show();
                    requestCreateRecord(model.getRecordName());

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // MP3 to OGG 변환 함수 호출
    private void convertAudio(String path, String folderId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Instant instant = Instant.now();
                unixTime = instant.getEpochSecond();

                String oggPath = path.replace(".tmp", ".ogg");

                String[] cmd = new String[]{"-i", path, "-vn", "-map_metadata", "-1", "-ac", "1", "-c:a", "libopus", "-b:a", "12k", "-application", "voip", oggPath};
                int rc = FFmpeg.execute(cmd);
                if (rc == 0) {
                    // 변환 성공
                    Log.d("AudioConverter", "변환 성공");
                } else {
                    // 변환 실패
                    Log.e("AudioConverter", "변환 실패");
                }

                uploadRecordFile(oggPath, folderId);

                // 변환 완료 후 UI 업데이트 (필요시)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // UI 업데이트 (예: ProgressBar 숨기기, 변환 완료 메시지 표시)
                        activity.dialog.dismiss();
                        activity.finish();
                    }
                });
            }
        });
    }


    public void docsUpload() {
        handlerThread = new HandlerThread("FFmpegThread");
        handlerThread.start();

        // Handler 생성 및 연결
        handler = new Handler(handlerThread.getLooper());

        if(activity.binding.edittextDocsUploadTitle.getText().length() != 0)
            model.setRecordName(activity.binding.edittextDocsUploadTitle.getText().toString().substring(0, 32));

        SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
        // Firebase Storage 참조 가져오기
        storageReference = FirebaseStorage.getInstance().getReference("folders");


        if(preferences.getString("userId", "").length() != 0) {
            userId = preferences.getString("userId", "");
        }

        if(preferences.getString("publicFolderId", "").length() != 0) {
            Folder folder = adapter.getItems().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition());
            folderId = String.valueOf(folder.getFolderId());
        }

        convertAudio(model.getRecordPath(), folderId);
    }

    public String getRecordPath() {
        return model.getRecordPath();
    }

    public String getRecordName() {
        return model.getRecordName();
    }
}
