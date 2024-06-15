package team.y2k2.globa.docs.upload;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.time.Instant;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsUploadActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;
//    private final static int REQUEST_CODE_UPLOAD_RECORD = 102;
    String recordName;
    String recordExtension;
    String recordPath;
    String userId;
    String folderId;
    long unixTime;



    DocsUploadFolderAdapter adapter;
    String firebasePath;

    ActivityDocsUploadBinding binding;

    private static boolean isAudioPlayed;

    List<FolderResponse> response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsUploadBinding.inflate(getLayoutInflater());

        setBundleParams();

        binding.textviewDocsAudioTitle.setText(recordName);
        binding.edittextDocsUploadTitle.setHint(recordName);

        binding.imagebuttonDocumentPlay.setOnClickListener(v -> {
            if(isAudioPlayed) {
                releaseMediaPlayer();
                binding.imagebuttonDocumentPlay.setImageResource(R.drawable.docs_play);
                isAudioPlayed = false;
                return;
            }

            binding.imagebuttonDocumentPlay.setImageResource(R.drawable.docs_pause);
            isAudioPlayed = true;
            playAudio(recordPath);
        });


        binding.linearlayoutDocsUploadConfirm.setOnClickListener(v -> {
            recordName = binding.edittextDocsUploadTitle.getText().toString();
            if(recordName.length() == 0) {
                recordName = binding.edittextDocsUploadTitle.getHint().toString();
            }

            SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
            // Firebase Storage 참조 가져오기
            storageReference = FirebaseStorage.getInstance().getReference("folders");

            if(preferences.getString("userId", "").length() != 0) {
                userId = preferences.getString("userId", "");
            }

            if(preferences.getString("publicFolderId", "").length() != 0) {
//                folderId = preferences.getString("publicFolderId", "");

                FolderResponse folder = adapter.getItems().get(binding.spinnerDocsUpload.getSelectedItemPosition());
                folderId = String.valueOf(folder.getFolderId());
            }

            uploadRecordFile(recordPath, userId, folderId, recordExtension);
            finish();
        });


        loadFolder();
        setContentView(binding.getRoot());

    }

    private void requestCreateRecord(String title) {
        // 네트워크 요청 보내기
        String path = "folders/" + folderId + "/" + unixTime + ".ogg";
        String folderId = Integer.toString(response.get(binding.spinnerDocsUpload.getSelectedItemPosition()).getFolderId());

        ApiClient apiClient = new ApiClient(this);

        apiClient.requestCreateRecord(folderId, title, path, "0");

        finish();
    }

    private void playAudio(String audioPath) {
        try {
            // MediaPlayer 객체를 생성하고 설정합니다.
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(audioPath));
            mediaPlayer.prepare();
            mediaPlayer.start();

            // 오디오 재생이 끝나면 MediaPlayer를 해제합니다.
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // 재생이 끝나면 MediaPlayer를 해제합니다.
                    releaseMediaPlayer();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "오디오 재생 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadRecordFile(String path, String userId, String folderId, String extension) {
        Log.d(getClass().getName(), "path 값 : " + path);
        Instant instant = Instant.now();
        unixTime = instant.getEpochSecond();

        String oggPath = path.split("\\.")[0] + ".ogg";
        convertMp3ToOgg(path,oggPath);

        firebasePath = folderId + "/" + unixTime + ".ogg";
        StorageReference audioRef = storageReference.child(firebasePath);

        // 예제로 진행하려면 raw 리소스에서 파일을 가져오고 Uri를 생성하여 사용할 수 있습니다.
        Uri uri = Uri.fromFile(new File(oggPath));

        // 파일 업로드
        audioRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 업로드 성공 시
                    Toast.makeText(getApplicationContext(), "파일 업로드 성공", Toast.LENGTH_SHORT).show();
                    requestCreateRecord(recordName);

                })
                .addOnFailureListener(e -> {
                    // 업로드 실패 시
                    Toast.makeText(getApplicationContext(), "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                });
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 액티비티가 정지될 때 MediaPlayer를 해제합니다.
        releaseMediaPlayer();
    }

    public void loadFolder() {
        ApiClient apiClient = new ApiClient(this);
        response = apiClient.requestGetFolders(1, 10);

        if (response != null) {
            adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, response);
            adapter.setDropDownViewResource(R.layout.item_folder);
            binding.spinnerDocsUpload.setAdapter(adapter);
            binding.spinnerDocsUpload.setSelection(0);
        }
    }

    public void setBundleParams() {
        Intent intent = getIntent();
        recordName = intent.getStringExtra("recordName").split("\\.")[0];
        recordExtension = intent.getStringExtra("recordName").split("\\.")[1];
        recordPath = intent.getStringExtra("recordPath");
    }

    public static void convertMp3ToOgg(String mp3FilePath, String oggFilePath) {
        String[] cmd = new String[]{"-i", mp3FilePath, "-vn", "-map_metadata", "-1", "-ac", "1", "-c:a", "libopus", "-b:a", "12k", "-application", "voip", oggFilePath};
        int rc = FFmpeg.execute(cmd);
        if (rc == 0) {
            // 변환 성공
            Log.d("AudioConverter", "변환 성공");
        } else {
            // 변환 실패
            Log.e("AudioConverter", "변환 실패");
        }
    }
}
