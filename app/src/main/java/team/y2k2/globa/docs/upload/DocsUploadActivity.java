package team.y2k2.globa.docs.upload;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.time.Instant;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;
import team.y2k2.globa.main.folder.FolderAdapter;
import team.y2k2.globa.main.folder.FolderModel;
import team.y2k2.globa.main.folder.FolderResponse;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyAdapter;
import team.y2k2.globa.main.folder.currently.FolderCurrentlyModel;

public class DocsUploadActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;

    private final static int REQUEST_CODE_UPLOAD_RECORD = 102;
    String recordName;
    String recordExtension;
    String recordPath;
    String userId;
    String folderId;
    Instant unixTime;

    String firebasePath;

    ActivityDocsUploadBinding binding;

    private static boolean isAudioPlayed;

    // Retrofit 인스턴스 생성
    Retrofit retrofit;
    SharedPreferences preferences;
    String authorization;
    ApiService apiService;

    List<FolderResponse> responses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsUploadBinding.inflate(getLayoutInflater());

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        authorization = "Bearer " + preferences.getString("accessToken", "");
        apiService = retrofit.create(ApiService.class);

        Intent intent = getIntent();
        recordName = intent.getStringExtra("recordName").split("\\.")[0];
        recordExtension = intent.getStringExtra("recordName").split("\\.")[1];
        recordPath = intent.getStringExtra("recordPath");

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
            storageReference = FirebaseStorage.getInstance().getReference("users");
            folderId = "test_folder";

            if(preferences.getString("uid", "").length() != 0) {
                userId = preferences.getString("uid", "");
            }

            uploadRecordFile(recordPath, userId, folderId, recordExtension);

            finish();
        });


        loadFolder();
        setContentView(binding.getRoot());

    }

    private void requestCreateRecord(String title, String path) {
        // 네트워크 요청 보내기
        RecordCreateRequest request = new RecordCreateRequest(title, path, "0");
        Log.d("Record UPLOAD", "업로드: " + title + " | " + path);

        String folderId = Integer.toString(responses.get(binding.spinnerDocsUpload.getSelectedItemPosition()).getFolderId());

        Call<Void> call = apiService.requestCreateRecord(folderId,"application/json", authorization, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때 처리할 내용
                    response.body();

                    Log.d("Record UPLOAD", "업로드 완료 : " + response.code());
                    finish();

                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리할 내용
                    Log.d("Record UPLOAD", "업로드 실패 : " + response.code() + " | " + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 요청 실패 시 처리할 내용
                Log.d("Record UPLOAD", "업로드 실패 : " + t.getMessage());
            }
        });
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
        unixTime = Instant.now();

        firebasePath = userId + "/" + folderId + "/" + unixTime + "."+ extension;
        StorageReference audioRef = storageReference.child(firebasePath);

        // 예제로 진행하려면 raw 리소스에서 파일을 가져오고 Uri를 생성하여 사용할 수 있습니다.
        Uri uri = Uri.fromFile(new File(path));

        // 파일 업로드
        audioRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 업로드 성공 시
                        Toast.makeText(getApplicationContext(), "파일 업로드 성공", Toast.LENGTH_SHORT).show();
                        requestCreateRecord(recordName, firebasePath);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 업로드 실패 시
                        Toast.makeText(getApplicationContext(), "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
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
        Call<List<FolderResponse>> call = apiService.requestGetFolders("application/json",authorization, 1, 10);
        call.enqueue(new Callback<List<FolderResponse>>() {
            @Override
            public void onResponse(Call<List<FolderResponse>> call, Response<List<FolderResponse>> response) {
                if (response.isSuccessful()) {
                    responses = response.body();
                    if (responses != null) {
                        DocsUploadFolderAdapter adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, responses);
                        adapter.setDropDownViewResource(R.layout.item_folder);
                        binding.spinnerDocsUpload.setAdapter(adapter);
                        binding.spinnerDocsUpload.setSelection(0);
                    }
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d("FOLDERTEST", "오류");
                }
            }

            @Override
            public void onFailure(Call<List<FolderResponse>> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d("FOLDERTEST", "오류" + t.getMessage());

            }
        });
    }
}
