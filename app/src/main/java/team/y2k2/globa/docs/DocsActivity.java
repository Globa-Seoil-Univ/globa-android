package team.y2k2.globa.docs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.detail.DocsDetailViewModel;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.util.i18n.DateTimeFormatter;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ActivityDocsBinding binding;
    private DocsActivityModel viewModel;
    private DocsDetailViewModel docsDetailViewModel; // 댓글 추가/삭제 후 새로고침을 위함

    private SimpleExoPlayer player;
    private Runnable updateSeekbarRunnable;

    private String folderId, recordId;
    private String profile, name; // Adapter에 전달할 사용자 정보
    private long startTime;

    private final ActivityResultLauncher<Intent> moreActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("updatedTitle")) {
                        String updatedTitle = data.getStringExtra("updatedTitle");
                        viewModel.setTitle(updatedTitle);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DocsActivityModel.class);
        docsDetailViewModel = new ViewModelProvider(this).get(DocsDetailViewModel.class);
        player = new SimpleExoPlayer.Builder(this).build();

        setupWindowInsets();

        if (!getIntentData()) {
            return; // 필수 데이터 없으면 액티비티 종료
        }

        loadUserInfo();

        setupUI();
        observeViewModel();

        startTime = System.currentTimeMillis();

        viewModel.fetchData(this, folderId, recordId);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.constraintlayoutDocs.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.constraintlayoutDocs.getLayoutParams();
            if (params != null) {
                params.bottomMargin = systemBars.bottom;
                binding.constraintlayoutDocs.setLayoutParams(params);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private boolean getIntentData() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        recordId = intent.getStringExtra("recordId");
        folderId = intent.getStringExtra("folderId");

        if (data != null) {
            List<String> pathSegments = data.getPathSegments();
            if (pathSegments != null && pathSegments.size() == 3 && "docs".equals(pathSegments.get(1))) {
                folderId = pathSegments.get(0);
                recordId = pathSegments.get(2);
            }
        }

        if (folderId == null || recordId == null || folderId.isEmpty() || recordId.isEmpty()) {
            Toast.makeText(this, "문서 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private void loadUserInfo() {
        UserApiClient userApiClient = new UserApiClient(this);
        UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();
        if (userInfoResponse != null) {
            profile = userInfoResponse.getProfile();
            name = userInfoResponse.getName();
        } else {
            profile = "";
            name = "Unknown";
        }
    }

    private void setupUI() {
        binding.imageButtonDocsBack.setOnClickListener(v -> finish());
        binding.imageviewDocsMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocsMoreActivity.class);
            intent.putExtra("title", viewModel.getTitle().getValue());
            intent.putExtra("recordId", recordId);
            intent.putExtra("folderId", folderId);
            intent.putExtra("folderTitle", viewModel.getFolderTitle().getValue());
            moreActivityLauncher.launch(intent);
        });

        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(this));
        showDescription(); // 기본으로 상세 내용 표시

        binding.buttonDocsDescription.setOnClickListener(v -> showDescription());
        binding.buttonDocsSummary.setOnClickListener(v -> showSummary());
    }

    private void observeViewModel() {
        viewModel.getTitle().observe(this, title -> binding.textviewDocsTitle.setText(title));

        viewModel.getDetailItems().observe(this, items -> {
            DocsDetailAdapter detailAdapter = new DocsDetailAdapter(items, this);
            if (binding.buttonDocsDescription.getCurrentTextColor() == Color.WHITE) {
                binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
            }
        });

        viewModel.getSummaryItems().observe(this, items -> {
            DocsSummaryAdapter summaryAdapter = new DocsSummaryAdapter(items);
            if (binding.buttonDocsSummary.getCurrentTextColor() == Color.WHITE) {
                binding.recyclerviewDocsDetail.setAdapter(summaryAdapter);
            }
        });

        viewModel.getAudioUrl().observe(this, this::loadAudio);
        viewModel.getErrorMessage().observe(this, error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
        viewModel.getIsLoading().observe(this, isLoading -> { });

        docsDetailViewModel.getIsFirstCommentLiveData().observe(this, isFirst -> {
            if (isFirst) {
                viewModel.fetchData(this, folderId, recordId);
                docsDetailViewModel.setIsFirstCommentLiveData(false);
            }
        });
        docsDetailViewModel.getIsAllDeletedLiveData().observe(this, isAllDeleted -> {
            if (isAllDeleted) {
                viewModel.fetchData(this, folderId, recordId);
                docsDetailViewModel.setIsAllDeletedLiveData(false);
            }
        });
    }

    private void loadAudio(String audioUrl) {
        if (audioUrl == null || audioUrl.isEmpty()) {
            binding.lottieAudioDownload.setVisibility(View.GONE);
            return;
        }

        binding.lottieAudioDownload.setVisibility(View.VISIBLE);
        FirebaseStorage.getInstance().getReference().child(audioUrl).getDownloadUrl()
                .addOnSuccessListener(this::setupPlayer)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "음성 파일을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    binding.lottieAudioDownload.setVisibility(View.GONE);
                });
    }

    private void setupPlayer(Uri uri) {
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    binding.lottieAudioDownload.setVisibility(View.GONE);

                    binding.imageButtonDocumentAudioPlay.setVisibility(View.VISIBLE);
                    binding.imageviewDocumentReplay.setVisibility(View.VISIBLE);
                    binding.imageviewDocumentForward.setVisibility(View.VISIBLE);

                    binding.seekbarAudioProgress.setMax((int) player.getDuration());
                    binding.textviewDocumentAudioEndTime.setText(DateTimeFormatter.getTimeFormat((int) player.getDuration()));
                }
            }
        });

        binding.imageButtonDocumentAudioPlay.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                binding.imageButtonDocumentAudioPlay.setImageResource(R.drawable.docs_play);
                stopUpdatingSeekBar();
            } else {
                player.play();
                binding.imageButtonDocumentAudioPlay.setImageResource(R.drawable.docs_pause);
                startUpdatingSeekBar();
            }
        });

        binding.imageviewDocumentForward.setOnClickListener(v -> seekTo(getCurrentPosition() + 5000));
        binding.imageviewDocumentReplay.setOnClickListener(v -> seekTo(getCurrentPosition() - 5000));

        binding.seekbarAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    player.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void showSummary() {
        binding.buttonDocsSummary.setTextColor(Color.WHITE);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsDescription.setTextColor(Color.BLACK);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button);
        binding.recyclerviewDocsDetail.setAdapter(new DocsSummaryAdapter(viewModel.getSummaryItems().getValue()));
    }

    public void showDescription() {
        binding.buttonDocsDescription.setTextColor(Color.WHITE);
        binding.buttonDocsDescription.setBackgroundResource(R.drawable.main_button_selected);
        binding.buttonDocsSummary.setTextColor(Color.BLACK);
        binding.buttonDocsSummary.setBackgroundResource(R.drawable.main_button);
        binding.recyclerviewDocsDetail.setAdapter(new DocsDetailAdapter(viewModel.getDetailItems().getValue(), this));
    }

    // --- ExoPlayer & SeekBar Control ---
    public void startUpdatingSeekBar() {
        updateSeekbarRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int currentPosition = (int) player.getCurrentPosition();
                    binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(currentPosition));
                    binding.seekbarAudioProgress.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekbarRunnable);
    }
    public void stopUpdatingSeekBar() { if(updateSeekbarRunnable != null) handler.removeCallbacks(updateSeekbarRunnable); }
    public void setDuration(int seconds) { seekTo(seconds * 1000); }
    @Override public void start() { if (player != null) player.play(); }
    @Override public void pause() { if (player != null) player.pause(); }
    @Override public int getDuration() { return player != null ? (int) player.getDuration() : 0; }
    @Override public int getCurrentPosition() { return player != null ? (int) player.getCurrentPosition() : 0; }
    @Override public void seekTo(int pos) { if (player != null) player.seekTo(Math.max(0, Math.min(pos, getDuration()))); }
    @Override public boolean isPlaying() { return player != null && player.isPlaying(); }
    @Override public int getBufferPercentage() { return player != null ? player.getBufferedPercentage() : 0; }
    @Override public boolean canPause() { return true; }
    @Override public boolean canSeekBackward() { return true; }
    @Override public boolean canSeekForward() { return true; }
    @Override public int getAudioSessionId() { return player != null ? player.getAudioSessionId() : 0; }

    // --- Lifecycle Methods ---
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) player.pause();
        stopUpdatingSeekBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingSeekBar();
        if (player != null) {
            player.release();
            player = null;
        }
        long endTime = System.currentTimeMillis();
        long durationMilliSecond = endTime - startTime;
        int durationMinute = (int) (durationMilliSecond / 60000);
        new RecordApiClient(this).updateStudyTime(folderId, recordId, String.valueOf(durationMinute));
    }

    public String getFolderId() { return folderId; }
    public String getRecordId() { return recordId; }
    public String getProfile() { return profile; }
    public String getName() { return name; }
}