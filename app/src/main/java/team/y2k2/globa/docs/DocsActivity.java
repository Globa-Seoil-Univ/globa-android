package team.y2k2.globa.docs;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsBinding;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public ActivityDocsBinding binding;
    DocsViewModel viewModel;
    private SimpleExoPlayer player;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekbarRunnable;
    private long startTime, endTime;
    private String startDate;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

        // 프리퍼런스 헬퍼 호출
        preferencesHelper = new PreferencesHelper(this);

        // 파일이 열리는 시간 측정
        startTime = System.currentTimeMillis();
        Date date = new Date(startTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDate = dateFormat.format(date);
        // Log.d("시간, 날짜", "열린 시간: " + startTime + ", 날짜: " + startDate);

        viewModel = new ViewModelProvider(this).get(DocsViewModel.class);
        viewModel.setActivity(this);
        viewModel.setBinding(binding);

        player = new SimpleExoPlayer.Builder(this).build();

        binding.textviewDocsTitle.setText(viewModel.getTitle());
        viewModel.getResponse();

        binding.imagebuttonDocsBack.setOnClickListener(v -> {
            player.stop();
            finish();
        });

        binding.imageviewDocsMore.setOnClickListener(v -> {
            startActivity(viewModel.getDocsMoreIntent());
        });

        binding.buttonDocsDescription.setOnClickListener(v -> {
            showDescription();
        });

        binding.buttonDocsSummary.setOnClickListener(v -> {
            showSummary();
        });

        setContentView(binding.getRoot());
    }

    public void setDuration(int second) {
        int position = second * 1000;

        if (player.getDuration() <= position) {
            position = (int) player.getDuration();
        }

        binding.seekbarAudioProgress.setProgress(position);
        binding.textviewDocumentAudioNowTime.setText(formatDuration(position));
        player.seekTo(position);
    }

    public void showSummary() {
        binding.buttonDocsSummary.setTextColor(Color.WHITE);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsDescription.setTextColor(Color.BLACK);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button);

        binding.recyclerviewDocsDetail.setAdapter(viewModel.getSummaryAdapter());
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }

    public void showDescription() {
        binding.buttonDocsDescription.setTextColor(Color.WHITE);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsSummary.setTextColor(Color.BLACK);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button);

        binding.recyclerviewDocsDetail.setAdapter(viewModel.getDetailAdapter());
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }



    public static String formatDuration(int durationMillis) {
        int hours = (durationMillis / 1000) / 3600;
        durationMillis %= 1000 * 3600;

        int minutes = (durationMillis / 1000) / 60;
        int seconds = (durationMillis / 1000) % 60;

        if (hours > 0)
            return String.format("%2d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void start() {
        player.play();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        return (int) player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return player.getBufferedPercentage();
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
        return player.getAudioSessionId();
    }

    public void startUpdatingSeekBar() {
        updateSeekbarRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int currentPosition = (int) player.getCurrentPosition();
                    binding.textviewDocumentAudioNowTime.setText(formatDuration(currentPosition));
                    binding.seekbarAudioProgress.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        };
        handler.post(updateSeekbarRunnable);
    }

    public void stopUpdatingSeekBar() {
        handler.removeCallbacks(updateSeekbarRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdatingSeekBar();
        if (Util.SDK_INT <= 23) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopUpdatingSeekBar();
        if (Util.SDK_INT > 23) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingSeekBar();
        player.release();
        player = null;

        // 문서 상세 보기 종료 시
        endTime = System.currentTimeMillis();
        long durationMilliSecond = endTime - startTime;
        int durationMinute = (int)(durationMilliSecond / 60000);
//        Log.d("시간", "열려 있던 시간(분): " + durationMinute);
        preferencesHelper.addData(startDate, durationMinute);
    }

    public String getFolderId() {
        return viewModel.getFolderId();
    }

    public String getRecordId() {
        return viewModel.getRecordId();
    }
}
