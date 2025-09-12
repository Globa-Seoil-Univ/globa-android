package team.y2k2.globa.docs.upload;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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
    private final MutableLiveData<Boolean> isUploading = new MutableLiveData<>(false);

    private DocsUploadModel model;
    private DocsUploadActivity activity;
    private StorageReference storageReference;
    private String folderId;
    private FolderResponse folderResponse;
    private DocsUploadFolderAdapter adapter;
    private MediaPlayer mediaPlayer;
    private long unixTime;
    private Handler handler;
    private FolderApiClient folderApiClient;
    private RecordApiClient recordApiClient;
    private DocsUploadLanguageAdapter languageAdapter;

    public LiveData<String> getDocsTitle() { return title; }
    public LiveData<AudioPlayState> getAudioPlayState() { return audioPlayState; }
    public LiveData<String> getUploadStatus() { return uploadStatus; }
    public LiveData<Boolean> getIsUploading() { return isUploading; }

    public void setActivity(DocsUploadActivity activity) {
        this.activity = activity;
        this.model = new DocsUploadModel(activity.getIntent());
        this.folderApiClient = new FolderApiClient();
        this.recordApiClient = new RecordApiClient();
        setDocsTitle();
        audioPlayState.setValue(AudioPlayState.STOPPED);
    }

    private void setDocsTitle() {
        String docTitle = model.getRecordName();
        if (docTitle != null && docTitle.length() >= 20) {
            docTitle = docTitle.substring(0, 20);
        }
        this.title.setValue(docTitle);
    }

    public void docsUploadConfirm() {
        isUploading.setValue(true);
        uploadStatus.postValue("LOADING");

        HandlerThread handlerThread = new HandlerThread("FFmpegThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        if (activity.binding.edittextDocsUploadTitle.getText().length() != 0)
            model.setRecordName(activity.binding.edittextDocsUploadTitle.getText().toString());

        storageReference = FirebaseStorage.getInstance().getReference("folders");

        if (adapter.getItems() != null && !adapter.getItems().isEmpty()) {
            Folder folder = adapter.getItems().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition());
            folderId = String.valueOf(folder.getFolderId());
        } else {
            // 선택할 폴더가 없는 경우 처리
            uploadStatus.postValue("업로드할 폴더가 없습니다.");
            isUploading.postValue(false);
            return;
        }

        convertAudio(model.getRecordPath(), folderId);
    }

    private void convertAudio(String path, String folderId) {
        handler.post(() -> {
            Instant instant = Instant.now();
            unixTime = instant.getEpochSecond();
            String oggPath = path.replace(".tmp", ".ogg");
            String[] cmd = {"-i", path, "-vn", "-map_metadata", "-1", "-ac", "1", "-c:a", "libopus", "-b:a", "12k", "-application", "voip", oggPath};
            String command = String.join(" ", cmd);

            FFmpegSession session = FFmpegKit.execute(command);
            ReturnCode returnCode = session.getReturnCode();

            if (ReturnCode.isSuccess(returnCode)) {
                Log.d("AudioConverter", "변환 성공");
                uploadRecordFile(oggPath, folderId);
            } else {
                Log.e("AudioConverter", "변환 실패, code: " + returnCode.getValue());
                uploadStatus.postValue("오디오 변환 실패");
                isUploading.postValue(false);
            }
        });
    }

    public void uploadRecordFile(String oggPath, String folderId) {
        this.folderId = folderId;
        String firebasePath = folderId + "/" + unixTime + ".ogg";
        StorageReference audioRef = storageReference.child(firebasePath);
        Uri uri = Uri.fromFile(new File(oggPath));

        audioRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            isUploading.postValue(false);
            uploadStatus.postValue("파일 업로드 성공");

            String lang = "ko";
//            switch (activity.binding.spinnerDocsUploadLanguage.getSelectedItemPosition()) {
//                case 1: lang = "en"; break;
//                case 2: lang = "ja"; break;
//                default: lang = "ko"; break;
//            }
            requestCreateRecord(model.getRecordName(), lang);
        }).addOnFailureListener(e -> {
            isUploading.postValue(false);
            uploadStatus.postValue("파일 업로드 실패: " + e.getMessage());
        });
    }

    private void requestCreateRecord(String title, String lang) {
        if(this.folderResponse != null && this.folderResponse.getFolders() != null) {
            String selectedFolderId = this.folderResponse.getFolders().get(activity.binding.spinnerDocsUpload.getSelectedItemPosition()).getFolderId();
            String path = "folders/" + selectedFolderId + "/" + unixTime + ".ogg";
            recordApiClient.requestCreateRecord(selectedFolderId, title, path, lang);
        }
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
            uploadStatus.postValue("오디오 재생 오류: " + e.getMessage());
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioPlayState.postValue(AudioPlayState.PAUSED);
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioPlayState.postValue(AudioPlayState.STOPPED);
        }
    }

    public void loadFolder() {
        folderResponse = folderApiClient.requestGetFolders(1, 100);
        if(folderResponse != null && folderResponse.getFolders() != null) {
            adapter = new DocsUploadFolderAdapter(activity, R.layout.item_folder, folderResponse.getFolders());
            adapter.setDropDownViewResource(R.layout.item_folder);
            activity.binding.spinnerDocsUpload.setAdapter(adapter);
            activity.binding.spinnerDocsUpload.setSelection(0);
        }
    }

    public void loadLanguage() {
        languageAdapter = new DocsUploadLanguageAdapter(activity, R.layout.item_language);
        languageAdapter.setDropDownViewResource(R.layout.item_language);
//        activity.binding.spinnerDocsUploadLanguage.setAdapter(languageAdapter);
//        activity.binding.spinnerDocsUploadLanguage.setSelection(0);
    }
}