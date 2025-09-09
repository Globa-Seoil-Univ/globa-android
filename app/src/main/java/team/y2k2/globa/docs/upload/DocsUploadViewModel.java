package team.y2k2.globa.docs.upload;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.Instant;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.FolderApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsUploadViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<AudioPlayState> audioPlayState = new MutableLiveData<>();
    private final MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    FolderResponse response;
    private DocsUploadModel model;
    private DocsUploadActivity activity;
    private StorageReference storageReference;
    private String folderId;
    private DocsUploadFolderAdapter adapter;
    private MediaPlayer mediaPlayer;
    private long unixTime;
    private Handler handler;
    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;
    private DocsUploadLanguageAdapter languageAdapter;

    public LiveData<String> getDocsTitle() {
        return title;
    }

    public LiveData<AudioPlayState> getAudioPlayState() {
        return audioPlayState;
    }

    public LiveData<String> getUploadStatus() {
        return uploadStatus;
    }

    public void setActivity(DocsUploadActivity activity) {
        this.activity = activity;
        this.model = new DocsUploadModel(activity.getIntent());
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient();
        setDocsTitle();
        audioPlayState.setValue(AudioPlayState.STOPPED);
    }

    private void setDocsTitle() {
        String title;
        if (model.getRecordName().length() >= 20) {
            title = model.getRecordName().substring(0, 20);
        } else {
            title = model.getRecordName();
        }
        this.title.setValue(title);
    }

    public void onPlayButtonClick() {
        if (audioPlayState.getValue() == AudioPlayState.PLAYING) {
            pauseAudio();
        } else {
            playAudio();
        }
    }

    private void playAudio() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(activity, Uri.parse(model.getRecordPath()));
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
            audioPlayState.postValue(AudioPlayState.PLAYING);

            mediaPlayer.setOnCompletionListener(v -> releaseMediaPlayer());
        } catch (Exception e) {
            uploadStatus.postValue("오류 발생" + e);
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioPlayState.postValue(AudioPlayState.PAUSED);
        }
    }


    private void requestCreateRecord(String title, String lang) {
        // 네트워크 요청 보내기
        String path = "folders/" + folderId + "/" + unixTime + ".ogg";
        String folderId = Integer.toString(Integer.parseInt(response.getFolders().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition()).getFolderId()));

        recordApiClient.requestCreateRecord(folderId, title, path, lang);
        activity.finish();
    }

    public void loadFolder() {
        response = folderApiClient.requestGetFolders(1, 100);
        adapter = new DocsUploadFolderAdapter(activity, R.layout.item_folder, response.getFolders());
        adapter.setDropDownViewResource(R.layout.item_folder);
        activity.binding.spinnerDocsUpload.setAdapter(adapter);
        activity.binding.spinnerDocsUpload.setSelection(0);
    }

    public void loadLanguage() {
        languageAdapter = new DocsUploadLanguageAdapter(activity, R.layout.item_language);
        languageAdapter.setDropDownViewResource(R.layout.item_language);
        activity.binding.spinnerDocsUploadLanguage.setAdapter(languageAdapter);
        activity.binding.spinnerDocsUploadLanguage.setSelection(0);
    }

    public void uploadRecordFile(String oggPath, String folderId) {
        this.folderId = folderId;
        String firebasePath = folderId + "/" + unixTime + ".ogg";
        StorageReference audioRef = storageReference.child(firebasePath);

        Log.d(getClass().getName(), "firebase path : " + firebasePath);

        Uri uri = Uri.fromFile(new File(oggPath));

        audioRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            String lang;
            switch (activity.binding.spinnerDocsUploadLanguage.getSelectedItemPosition()) {
                case 0 : lang = "ko"; break;
                case 1 : lang = "en"; break;
                case 2 : lang = "ja"; break;
                default: lang = "ko"; break;
            }

            // 업로드 성공 시
            Toast.makeText(activity, "파일 업로드 성공", Toast.LENGTH_SHORT).show();
            if (model.getRecordName().length() >= 20) {

                requestCreateRecord(model.getRecordName().substring(0, 20), lang);
            }
            else requestCreateRecord(model.getRecordName(), lang);


        }).addOnFailureListener(e -> Toast.makeText(activity, "파일 업로드 실패", Toast.LENGTH_SHORT).show());
    }

    // MP3 to OGG 변환 함수 호출
    private void convertAudio(String path, String folderId) {
        handler.post(() -> {
            Instant instant = Instant.now();
            unixTime = instant.getEpochSecond();

            String oggPath = path.replace(".tmp", ".ogg");
            String[] cmd = new String[]{"-i", path, "-vn", "-map_metadata", "-1", "-ac", "1", "-c:a", "libopus", "-b:a", "12k", "-application", "voip", oggPath};
            String ffmpegCommand = String.join(" ", cmd); // 명령 배열을 문자열로 변환

            FFmpegSession session = FFmpegKit.execute(ffmpegCommand);
            ReturnCode returnCode = session.getReturnCode();

            if (ReturnCode.isSuccess(returnCode)) {
                Log.d("AudioConverter", "변환 성공");
            } else {
                Log.e("AudioConverter", "변환 실패, code: " + returnCode.getValue());
            }

            uploadRecordFile(oggPath, folderId);

            // 변환 완료 후 UI 업데이트 (필요시)
            activity.runOnUiThread(() -> {
                // UI 업데이트 (예: ProgressBar 숨기기, 변환 완료 메시지 표시)
                activity.finish();
            });
        });
    }


    public void docsUploadConfirm() {
        HandlerThread handlerThread = new HandlerThread("FFmpegThread");
        handlerThread.start();

        // Handler 생성 및 연결
        handler = new Handler(handlerThread.getLooper());

        if (activity.binding.edittextDocsUploadTitle.getText().length() != 0)
            model.setRecordName(activity.binding.edittextDocsUploadTitle.getText().toString());

        SharedPreferences preferences = activity.getSharedPreferences("account", Activity.MODE_PRIVATE);
        // Firebase Storage 참조 가져오기
        storageReference = FirebaseStorage.getInstance().getReference("folders");

        if (!preferences.getString("publicFolderId", "").isEmpty()) {
            Folder folder = adapter.getItems().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition());
            folderId = String.valueOf(folder.getFolderId());
        }

        convertAudio(model.getRecordPath(), folderId);
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioPlayState.postValue(AudioPlayState.STOPPED);
        }
    }

}
