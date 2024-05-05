package team.y2k2.globa.docs;

import static okhttp3.internal.concurrent.TaskLoggerKt.formatDuration;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryModel;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    ActivityDocsBinding binding;
    DocsModel docsModel;
    DocsSummaryModel docsSummaryModel;

    Timer timer;

    String audioUrl;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    Boolean isMusicStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());
        docsModel = new DocsModel();
        docsSummaryModel = new DocsSummaryModel();

        Intent intent = getIntent();

        binding.textviewDocsTitle.setText(intent.getStringExtra("title").toString());
        audioUrl = intent.getStringExtra("audioUrl").toString();

        DocsDetailAdapter detailAdapter = new DocsDetailAdapter(docsModel.getItems());
        DocsSummaryAdapter summaryAdapter = new DocsSummaryAdapter(docsSummaryModel.getItems());

        binding.imagebuttonDocsBack.setOnClickListener(v -> {
            mediaPlayer.pause();
            timer.cancel();
            finish();
        });

        binding.buttonDocsDescription.setOnClickListener(v -> {
            binding.buttonDocsDescription.setTextColor(Color.WHITE);
            binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button_selected);
            binding.buttonDocsSummary.setTextColor(Color.BLACK);
            binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button);


            binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
            binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        });

        binding.buttonDocsSummary.setOnClickListener(v -> {
            binding.buttonDocsSummary.setTextColor(Color.WHITE);
            binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button_selected);
            binding.buttonDocsDescription.setTextColor(Color.BLACK);
            binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button);

            binding.recyclerviewDocsDetail.setAdapter(summaryAdapter);
            binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        });

        binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        // 오디오 관련 코드 - 시작

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
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
