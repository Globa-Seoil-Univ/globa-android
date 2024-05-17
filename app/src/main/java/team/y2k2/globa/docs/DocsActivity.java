package team.y2k2.globa.docs;

import static okhttp3.internal.concurrent.TaskLoggerKt.formatDuration;
import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryModel;
import team.y2k2.globa.main.notice.NoticeAutoScrollHandler;
import team.y2k2.globa.main.notice.NoticeFragmentAdapter;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public ActivityDocsBinding binding;
    DocsModel docsModel;
    DocsSummaryModel docsSummaryModel;
    String folderId;
    String recordId;

    Timer timer;

    String audioUrl;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    DocsDetailAdapter detailAdapter;
    DocsSummaryAdapter summaryAdapter;
    Boolean isMusicStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

        mediaPlayer = new MediaPlayer();



        Intent intent = getIntent();

        binding.textviewDocsTitle.setText(intent.getStringExtra("title").toString());
        audioUrl = intent.getStringExtra("audioUrl").toString();
        folderId = intent.getStringExtra("folderId").toString();
        recordId = intent.getStringExtra("recordId").toString();


        getResponse();

        binding.imagebuttonDocsBack.setOnClickListener(v -> {
            mediaPlayer.stop();
            timer.cancel();
            finish();
        });

        binding.imageviewDocsMore.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, DocsMoreActivity.class);
            startActivity(intent1);
        });

        binding.buttonDocsDescription.setOnClickListener(v -> {
            showDescription();
        });

        binding.buttonDocsSummary.setOnClickListener(v -> {
            showSummary();
        });

        // 오디오 관련 코드 - 시작

        mediaPlayer.setAudioAttributes(new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            mediaPlayer.setDataSource(this, Uri.parse(audioUrl));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // MediaPlayer가 준비되면 재생 시작
                    binding.seekbarAudioProgress.setMax(mp.getDuration());
                    binding.textviewDocumentAudioEndTime.setText(formatDuration(mp.getDuration()));
                    binding.textviewDocumentAudioNowTime.setText(formatDuration(0));

                    binding.lottieAudioDownload.setVisibility(View.INVISIBLE);

                    binding.imagebuttonDocumentAudioPlay.setVisibility(View.VISIBLE);
                    binding.imageviewDocumentReplay.setVisibility(View.VISIBLE);
                    binding.imageviewDocumentForward.setVisibility(View.VISIBLE);
                }
            });
        } catch (IOException e) {
            // 파일 재생 실패 시 예외 처리
            e.printStackTrace();
            Toast.makeText(DocsActivity.this, "음악 재생 실패", Toast.LENGTH_SHORT).show();
        }

        isMusicStarted = false;


        binding.imagebuttonDocumentAudioPlay.setOnClickListener(v -> {
            if(! isMusicStarted) {
                isMusicStarted = true;
                binding.imagebuttonDocumentAudioPlay.setImageResource(R.drawable.docs_pause);
                mediaPlayer.start();

                // 타이머 설정: 1초마다 현재 재생 시간 업데이트
                timer = new Timer();

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            int currentPosition = mediaPlayer.getCurrentPosition(); // 현재 재생 위치 가져오기
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.textviewDocumentAudioNowTime.setText(formatDuration(currentPosition));
                                    binding.seekbarAudioProgress.setProgress(currentPosition);
                                }
                            });
                        }
                    }
                }, 0, 1000); // 1초마다 실행
            } else {
                isMusicStarted = false;
                mediaPlayer.pause();
                binding.imagebuttonDocumentAudioPlay.setImageResource(R.drawable.docs_play);
                timer.cancel();
            }
        });

        binding.imageviewDocumentForward.setOnClickListener(v ->{
            int currentPosition = mediaPlayer.getCurrentPosition();
            int forwardPosition = currentPosition + 5000;

            if(mediaPlayer.getDuration() <= forwardPosition) {
                forwardPosition = mediaPlayer.getDuration();
            }

            binding.seekbarAudioProgress.setProgress(forwardPosition);
            binding.textviewDocumentAudioNowTime.setText(formatDuration(forwardPosition));
            mediaPlayer.seekTo(forwardPosition);
        });

        binding.imageviewDocumentReplay.setOnClickListener(v ->{
            int currentPosition = mediaPlayer.getCurrentPosition();
            int replayPosition = currentPosition - 5000;

            if(replayPosition < 0) {
                replayPosition = 0;
            }

            binding.seekbarAudioProgress.setProgress(replayPosition);
            binding.textviewDocumentAudioNowTime.setText(formatDuration(replayPosition));
            mediaPlayer.seekTo(replayPosition);
        });


        binding.seekbarAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.textviewDocumentAudioNowTime.setText(formatDuration(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                binding.textviewDocumentAudioNowTime.setText(formatDuration(progress));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                binding.textviewDocumentAudioNowTime.setText(formatDuration(progress));
                mediaPlayer.seekTo(progress);
            }
        });


        // 오디오 관련 코드 - 종료
        setContentView(binding.getRoot());
    }


    public void showSummary() {
        binding.buttonDocsSummary.setTextColor(Color.WHITE);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsDescription.setTextColor(Color.BLACK);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button);

        binding.recyclerviewDocsDetail.setAdapter(summaryAdapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }

    public void showDescription() {
        binding.buttonDocsDescription.setTextColor(Color.WHITE);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsSummary.setTextColor(Color.BLACK);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button);

        binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }


    public void getResponse() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Log.d(getClass().getName(), "folderId : " + folderId);
        Log.d(getClass().getName(), "recordId : " + recordId);

        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");

        Call<DocsDetailResponse> call = apiService.requestGetDocumentDetail(folderId, recordId, "application/json",authorization);
        call.enqueue(new Callback<DocsDetailResponse>() {
            @Override
            public void onResponse(Call<DocsDetailResponse> call, Response<DocsDetailResponse> response) {
                if (response.isSuccessful()) {
                    DocsDetailResponse docsDetailResponse = response.body();
                    // 성공적으로 응답을 받았을 때 처리

                    docsModel = new DocsModel(docsDetailResponse.getSections());
                    detailAdapter = new DocsDetailAdapter(docsModel.getItems(),DocsActivity.this);

                    binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
                    binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

                    docsSummaryModel = new DocsSummaryModel(docsDetailResponse.getSections());
                    summaryAdapter = new DocsSummaryAdapter(docsSummaryModel.getItems());
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d(getClass().getName(), "실패 : " + response.code());

                }
            }

            @Override
            public void onFailure(Call<DocsDetailResponse> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d(getClass().getName(), "실패 : " + t.getMessage());

            }
        });
    }


    private String formatDuration(int duration) {
        int seconds = duration / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // MediaController.MediaPlayerControl 인터페이스 구현
    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacksAndMessages(null);
    }

}
