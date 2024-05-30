package team.y2k2.globa.docs;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.more.DocsMoreViewModel;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryModel;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public ActivityDocsBinding binding;
    DocsModel docsModel;
    DocsSummaryModel docsSummaryModel;
    String title;
    String folderId;
    String recordId;
    String folderTitle;

    String audioUrl;
    private SimpleExoPlayer player;

    DocsDetailAdapter detailAdapter;
    DocsSummaryAdapter summaryAdapter;
    Boolean isMusicStarted;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekbarRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

        player = new SimpleExoPlayer.Builder(this).build();

        Intent intent = getIntent();

        title = intent.getStringExtra("title").toString();
        folderId = intent.getStringExtra("folderId").toString();
        recordId = intent.getStringExtra("recordId").toString();

        binding.textviewDocsTitle.setText(title);
        getResponse();

        binding.imagebuttonDocsBack.setOnClickListener(v -> {
            player.stop();
            finish();
        });

        binding.imageviewDocsMore.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, DocsMoreActivity.class);
            intent1.putExtra("title", title);
            intent1.putExtra("recordId", recordId);

            intent1.putExtra("folderId", folderId);
            intent1.putExtra("folderTitle", folderTitle);
            startActivity(intent1);
        });

        binding.buttonDocsDescription.setOnClickListener(v -> {
            showDescription();
        });

        binding.buttonDocsSummary.setOnClickListener(v -> {
            showSummary();
        });

        setContentView(binding.getRoot());
    }

    public void loadAudio() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child(audioUrl);

        audioRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String audioUrl = uri.toString();

                        player.setMediaItem(MediaItem.fromUri(audioUrl));
                        player.prepare();

                        player.addListener(new Player.Listener() {
                            @Override
                            public void onPlaybackStateChanged(int playbackState) {
                                if (playbackState == Player.STATE_READY) {
                                    Log.d(getClass().getName(), player.getDuration() + " sec");
                                    binding.seekbarAudioProgress.setMax((int) player.getDuration());
                                    binding.textviewDocumentAudioEndTime.setText(formatDuration((int) player.getDuration()));
                                    binding.textviewDocumentAudioNowTime.setText(formatDuration(0));

                                    binding.lottieAudioDownload.setVisibility(View.INVISIBLE);

                                    binding.imagebuttonDocumentAudioPlay.setVisibility(View.VISIBLE);
                                    binding.imageviewDocumentReplay.setVisibility(View.VISIBLE);
                                    binding.imageviewDocumentForward.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        isMusicStarted = false;

                        binding.imagebuttonDocumentAudioPlay.setOnClickListener(v -> {
                            if (!isMusicStarted) {
                                isMusicStarted = true;
                                binding.imagebuttonDocumentAudioPlay.setImageResource(R.drawable.docs_pause);
                                player.play();
                                startUpdatingSeekBar();

                            } else {
                                isMusicStarted = false;
                                player.pause();
                                binding.imagebuttonDocumentAudioPlay.setImageResource(R.drawable.docs_play);
                                stopUpdatingSeekBar();
                            }
                        });

                        binding.imageviewDocumentForward.setOnClickListener(v -> {
                            int currentPosition = (int) player.getCurrentPosition();
                            int forwardPosition = currentPosition + 5000;

                            if (player.getDuration() <= forwardPosition) {
                                forwardPosition = (int) player.getDuration();
                            }

                            binding.seekbarAudioProgress.setProgress(forwardPosition);
                            binding.textviewDocumentAudioNowTime.setText(formatDuration(forwardPosition));
                            player.seekTo(forwardPosition);
                        });

                        binding.imageviewDocumentReplay.setOnClickListener(v -> {
                            int currentPosition = (int) player.getCurrentPosition();
                            int replayPosition = currentPosition - 5000;

                            if (replayPosition < 0) {
                                replayPosition = 0;
                            }

                            binding.seekbarAudioProgress.setProgress(replayPosition);
                            binding.textviewDocumentAudioNowTime.setText(formatDuration(replayPosition));
                            player.seekTo(replayPosition);
                        });

                        binding.seekbarAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    binding.textviewDocumentAudioNowTime.setText(formatDuration(progress));
                                    player.seekTo(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("FirebaseStorage", "다운로드 URL 가져오기 실패", exception);
                    }
                });
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
        ApiClient apiClient = new ApiClient(this);

        DocsDetailResponse response = apiClient.requestGetDocumentDetail(folderId, recordId);

        docsModel = new DocsModel(response.getSections());
        detailAdapter = new DocsDetailAdapter(docsModel.getItems(), DocsActivity.this);
        folderTitle = response.getFolder().getTitle();


        binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        docsSummaryModel = new DocsSummaryModel(response.getSections());
        summaryAdapter = new DocsSummaryAdapter(docsSummaryModel.getItems());

        audioUrl = response.getPath();

        loadAudio();
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

    private void startUpdatingSeekBar() {
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

    private void stopUpdatingSeekBar() {
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
    }


}
