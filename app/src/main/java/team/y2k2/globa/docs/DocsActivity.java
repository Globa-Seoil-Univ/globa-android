package team.y2k2.globa.docs;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;

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
    RecordApiClient apiClient;
    UserApiClient userApiClient;
    SimpleDateFormat dateFormat;
    private SimpleExoPlayer player;
    private Runnable updateSeekbarRunnable;
    private long startTime;
    private String profile;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsBinding.inflate(getLayoutInflater());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.constraintlayoutDocs.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.constraintlayoutDocs.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            binding.constraintlayoutDocs.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });


        apiClient = new RecordApiClient();
        userApiClient = new UserApiClient();
        UserInfoResponse userInfoResponse = userApiClient.requestUserInfo();

        profile = userInfoResponse.getProfile();
        name = userInfoResponse.getName();

        // 파일이 열리는 시간 측정
        startTime = System.currentTimeMillis();
        dateFormat = DateTimeFormatter.getDateFormat(LanguageUtils.getCurrentLocale(this));

        viewModel = new ViewModelProvider(this).get(DocsActivityModel.class);
        docsDetailViewModel = new ViewModelProvider(this).get(DocsDetailViewModel.class);
        player = new SimpleExoPlayer.Builder(this).build();

        viewModel.setActivity(this);
        viewModel.setIntent(getIntent());
        viewModel.setPlayer(player);
        viewModel.setBinding(binding);
        viewModel.getResponse();

        docsDetailViewModel.getIsFirstCommentLiveData().observe(DocsActivity.this, isFirst -> {
            Log.d(getClass().getSimpleName(), "DocsActivity에서 첫 댓글 옵저버 시작");
            if (isFirst) {
                Log.d(getClass().getSimpleName(), "첫 댓글 감지 및 화면 다시 로드 시작");
                viewModel.getResponse();
                docsDetailViewModel.setIsFirstCommentLiveData(false);
            }
        });

        docsDetailViewModel.getIsAllDeletedLiveData().observe(DocsActivity.this, isAllDeleted -> {
            Log.d(getClass().getSimpleName(), "DocsActivity에서 모든 댓글 삭제 옵저버 시작");
            if (isAllDeleted) {
                Log.d(getClass().getSimpleName(), "모든 삭제 감지 및 화면 다시 로드 시작");
                viewModel.getResponse();
                docsDetailViewModel.setIsAllDeletedLiveData(false);
            }
        });

        binding.textviewDocsTitle.setText(viewModel.getTitle());

        binding.imageButtonDocsBack.setOnClickListener(v -> {
            player.stop();
            finish();
        });

        binding.imageviewDocsMore.setOnClickListener(v -> startActivity(viewModel.getDocsMoreIntent()));

        binding.buttonDocsDescription.setOnClickListener(v -> showDescription());

        binding.buttonDocsSummary.setOnClickListener(v -> showSummary());

        setContentView(binding.getRoot());

        // 문서 삭제 시
        docsMoreActivityModel = new ViewModelProvider(this).get(DocsMoreActivityModel.class);
        docsMoreActivityModel.setApiClient(this);
        docsMoreActivityModel.getIsDeleted().observe(DocsActivity.this, isDeleted -> {
            // 문서 더보기의 삭제여부 변수(LiveData) 관찰
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

    public void setDuration(int seconds) {
        int positionInMillis = seconds * 1000;

        if (player.getDuration() <= positionInMillis) {
            positionInMillis = (int) player.getDuration();
        }

        binding.seekbarAudioProgress.setProgress(positionInMillis);
        binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(positionInMillis));

        player.seekTo(positionInMillis);
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
                    binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(currentPosition));

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
        long endTime = System.currentTimeMillis();
        long durationMilliSecond = endTime - startTime;
        int durationMinute = (int) (durationMilliSecond / 60000);
        Log.d("시간", "열려 있던 시간(분): " + durationMinute);

        // durationMinute, dateFormat으로 공부시간 API 수정 필요
        Log.d(getClass().getSimpleName(), "공부 시간 수정 요청 (folderId: " + viewModel.getFolderId() + ", recordId: " + viewModel.getRecordId() + ", 분: " + durationMinute + ", dateFormat: " + dateFormat.format(new Date()) + ")");
        apiClient.updateStudyTime(viewModel.getFolderId(), viewModel.getRecordId(), String.valueOf(durationMinute));

        // detailAdapter에 생성된 disposable 메모리 해제
        viewModel.clearDisposable();
    }

    public String getFolderId() {
        return viewModel.getFolderId();
    }

    public String getRecordId() {
        return viewModel.getRecordId();
    }

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }
}
