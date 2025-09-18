package team.y2k2.globa.docs;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;
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

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.docs.detail.DocsDetailViewModel;
import team.y2k2.globa.docs.more.DocsMoreActivityModel;
import team.y2k2.globa.util.i18n.DateTimeFormatter;
import team.y2k2.globa.util.i18n.LanguageUtils;

public class DocsActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private final Handler handler = new Handler(Looper.getMainLooper());
    public ActivityDocsBinding binding;
    DocsActivityModel viewModel;
    DocsDetailViewModel docsDetailViewModel;
    DocsMoreActivityModel docsMoreActivityModel;
    RecordApiClient recordApiClient;
    UserApiClient userApiClient;
    SimpleDateFormat dateFormat;
    private SimpleExoPlayer player;
    private Runnable updateSeekbarRunnable;
    private long startTime;
    private String profile;
    private String name;

    private ActivityResultLauncher<Intent> moreActivityLauncher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

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

        moreActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("updatedTitle")) {
                            String updatedTitle = data.getStringExtra("updatedTitle");
                            binding.textviewDocsTitle.setText(updatedTitle);
                            viewModel.setTitle(updatedTitle);
                        }
                    }
                }
        );

        recordApiClient = new RecordApiClient(this);
        userApiClient = new UserApiClient(this);

        Intent intent = getIntent();
        Uri data = intent.getData();
        String recordId = intent.getStringExtra("recordId");
        String folderId = intent.getStringExtra("folderId");

        if (data != null) {
            // URI 형식: globa://folders/{folderId}/docs/{recordId}
            List<String> pathSegments = data.getPathSegments();
            if (pathSegments != null && pathSegments.size() == 3 && "docs".equals(pathSegments.get(1))) {
                folderId = pathSegments.get(0);
                recordId = pathSegments.get(2);
                Log.d(getClass().getSimpleName(), "딥링크로부터 ID 파싱 성공: folderId=" + folderId + ", recordId=" + recordId);
            }
        }

        if (folderId == null || recordId == null || folderId.isEmpty() || recordId.isEmpty()) {
            Toast.makeText(this, "문서 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "folderId 또는 recordId가 유효하지 않아 DocsActivity를 종료합니다.");
            finish();
            return;
        }

        UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();

        if (userInfoResponse != null) {
            profile = userInfoResponse.getProfile();
            name = userInfoResponse.getName();
        } else {
            Log.e(getClass().getSimpleName(), "사용자 정보 조회에 실패했습니다.");
            profile = "";
            name = "Unknown";
        }

        startTime = System.currentTimeMillis();
        dateFormat = DateTimeFormatter.getDateFormat(LanguageUtils.getCurrentLocale(this));

        viewModel = new ViewModelProvider(this).get(DocsActivityModel.class);
        docsDetailViewModel = new ViewModelProvider(this).get(DocsDetailViewModel.class);
        player = new SimpleExoPlayer.Builder(this).build();

        viewModel.setContext(this);
        viewModel.setActivity(this);
        viewModel.setIds(folderId, recordId);
        viewModel.setPlayer(player);
        viewModel.setBinding(binding);
        viewModel.getResponse();

        docsDetailViewModel.getIsFirstCommentLiveData().observe(this, isFirst -> {
            if (isFirst) {
                viewModel.getResponse();
                docsDetailViewModel.setIsFirstCommentLiveData(false);
            }
        });

        docsDetailViewModel.getIsAllDeletedLiveData().observe(this, isAllDeleted -> {
            if (isAllDeleted) {
                viewModel.getResponse();
                docsDetailViewModel.setIsAllDeletedLiveData(false);
            }
        });

        binding.textviewDocsTitle.setText(viewModel.getTitle());
        binding.imageButtonDocsBack.setOnClickListener(v -> {
            if (player != null) player.stop();
            finish();
        });
        binding.imageviewDocsMore.setOnClickListener(v -> {
            moreActivityLauncher.launch(viewModel.getDocsMoreIntent());
        });
        binding.buttonDocsDescription.setOnClickListener(v -> showDescription());
        binding.buttonDocsSummary.setOnClickListener(v -> showSummary());

        setContentView(binding.getRoot());

        docsMoreActivityModel = new ViewModelProvider(this).get(DocsMoreActivityModel.class);
        docsMoreActivityModel.setApiClient(this);
        docsMoreActivityModel.getIsDeleted().observe(this, isDeleted -> {
            if (isDeleted) {
                finish();
            }
        });
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

    @Override
    public void start() { if (player != null) player.play(); }

    @Override
    public void pause() { if (player != null) player.pause(); }

    @Override
    public int getDuration() { return player != null ? (int) player.getDuration() : 0; }

    public void setDuration(int seconds) {
        if (player == null) return;
        int positionInMillis = seconds * 1000;

        if (player.getDuration() <= positionInMillis) {
            positionInMillis = (int) player.getDuration();
        }

        binding.seekbarAudioProgress.setProgress(positionInMillis);
        binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(positionInMillis));

        player.seekTo(positionInMillis);
    }

    @Override
    public int getCurrentPosition() { return player != null ? (int) player.getCurrentPosition() : 0; }

    @Override
    public void seekTo(int pos) { if (player != null) player.seekTo(pos); }

    @Override
    public boolean isPlaying() { return player != null && player.isPlaying(); }

    @Override
    public int getBufferPercentage() { return player != null ? player.getBufferedPercentage() : 0; }

    @Override
    public boolean canPause() { return true; }

    @Override
    public boolean canSeekBackward() { return true; }

    @Override
    public boolean canSeekForward() { return true; }

    @Override
    public int getAudioSessionId() { return player != null ? player.getAudioSessionId() : 0; }

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

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdatingSeekBar();
        if (Util.SDK_INT <= 23 && player != null) player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopUpdatingSeekBar();
        if (Util.SDK_INT > 23 && player != null) player.pause();
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
        if (recordApiClient != null && viewModel != null) {
            recordApiClient.updateStudyTime(viewModel.getFolderId(), viewModel.getRecordId(), String.valueOf(durationMinute));
            viewModel.clearDisposable();
        }
    }

    public String getFolderId() { return viewModel.getFolderId(); }
    public String getRecordId() { return viewModel.getRecordId(); }
    public String getProfile() { return profile; }
    public String getName() { return name; }
}

