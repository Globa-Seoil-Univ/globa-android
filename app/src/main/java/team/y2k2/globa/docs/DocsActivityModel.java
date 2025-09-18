package team.y2k2.globa.docs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryModel;
import team.y2k2.globa.util.i18n.DateTimeFormatter;

public class DocsActivityModel extends ViewModel {
    String title;
    String folderId;
    String recordId;
    String folderTitle;
    DocsModel docsModel;
    String audioUrl;
    DocsDetailAdapter detailAdapter;
    DocsSummaryModel docsSummaryModel;
    Boolean isMusicStarted;
    ActivityDocsBinding binding;
    DocsSummaryAdapter summaryAdapter;
    private DocsActivity activity;
    private SimpleExoPlayer player;
    private Context context;

    private boolean isDownloadFailed = false;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setActivity(DocsActivity activity) {
        this.activity = activity;
    }

    public void setIds(String folderId, String recordId) {
        this.folderId = folderId;
        this.recordId = recordId;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    public void getResponse() {
        Log.d(getClass().getSimpleName(), "뷰모델 viewModel.getResponse() 시작");
        RecordApiClient apiClient = new RecordApiClient(context);

        Log.d(getClass().getName(), "folderId: " + folderId);
        Log.d(getClass().getName(), "recordId: " + recordId);

        DocsDetailResponse response = apiClient.requestGetDocumentDetail(folderId, recordId);

        if (response == null) {
            Log.e(getClass().getSimpleName(), "문서 상세 정보 조회에 실패했습니다.");
            return;
        }

        this.title = response.getTitle();

        docsModel = new DocsModel(response.getSections());

        detailAdapter = new DocsDetailAdapter(docsModel.getDetailItems(), activity);
        folderTitle = response.getFolder().getTitle();

        binding.recyclerviewDocsDetail.setAdapter(detailAdapter);
        binding.recyclerviewDocsDetail.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        docsSummaryModel = new DocsSummaryModel(response.getSections());
        summaryAdapter = new DocsSummaryAdapter(docsSummaryModel.getItems());

        audioUrl = response.getPath();
        loadAudio();
    }

    public Intent getDocsMoreIntent() {
        Intent intent = new Intent(activity, DocsMoreActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("recordId", recordId);
        intent.putExtra("folderId", folderId);
        intent.putExtra("folderTitle", folderTitle);

        return intent;
    }

    public void loadAudio() {
        if (audioUrl == null || audioUrl.isEmpty()) {
            Log.e(getClass().getName(), "오디오 URL이 비어있어 다운로드를 건너뜁니다.");
            binding.lottieAudioDownload.setVisibility(View.INVISIBLE);
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child(audioUrl);

        audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String audioUrl = uri.toString();

            player.setMediaItem(MediaItem.fromUri(audioUrl));
            player.prepare();

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        binding.seekbarAudioProgress.setMax((int) player.getDuration());
                        binding.textviewDocumentAudioEndTime.setText(DateTimeFormatter.getTimeFormat((int) player.getDuration()));
                        binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(0));
                        binding.lottieAudioDownload.setVisibility(View.INVISIBLE);
                        binding.imageButtonDocumentAudioPlay.setVisibility(View.VISIBLE);
                        binding.imageviewDocumentReplay.setVisibility(View.VISIBLE);
                        binding.imageviewDocumentForward.setVisibility(View.VISIBLE);
                    }
                }
            });

            isMusicStarted = false;

            binding.imageButtonDocumentAudioPlay.setOnClickListener(v -> {
                if (!isMusicStarted) {
                    isMusicStarted = true;
                    binding.imageButtonDocumentAudioPlay.setImageResource(R.drawable.docs_pause);
                    player.play();
                    activity.startUpdatingSeekBar();

                } else {
                    isMusicStarted = false;
                    player.pause();
                    binding.imageButtonDocumentAudioPlay.setImageResource(R.drawable.docs_play);
                    activity.stopUpdatingSeekBar();
                }
            });

            binding.imageviewDocumentForward.setOnClickListener(v -> {
                long currentPosition = player.getCurrentPosition();
                long forwardPosition = currentPosition + 5000;
                if (player.getDuration() <= forwardPosition) {
                    forwardPosition = player.getDuration();
                }
                binding.seekbarAudioProgress.setProgress((int)forwardPosition);
                binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat((int)forwardPosition));
                player.seekTo(forwardPosition);
            });

            binding.imageviewDocumentReplay.setOnClickListener(v -> {
                long currentPosition = player.getCurrentPosition();
                long replayPosition = currentPosition - 5000;
                if (replayPosition < 0) {
                    replayPosition = 0;
                }
                binding.seekbarAudioProgress.setProgress((int)replayPosition);
                binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat((int)replayPosition));
                player.seekTo(replayPosition);
            });

            binding.seekbarAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        binding.textviewDocumentAudioNowTime.setText(DateTimeFormatter.getTimeFormat(progress));
                        player.seekTo(progress);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }).addOnFailureListener(e -> {
            Log.e(getClass().getName(), "Firebase Storage에서 오디오 URL 다운로드 실패", e);

            Toast.makeText(context, "음성 파일을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();

            binding.lottieAudioDownload.setVisibility(View.INVISIBLE);
            binding.imageButtonDocumentAudioPlay.setVisibility(View.INVISIBLE);
            binding.imageviewDocumentReplay.setVisibility(View.INVISIBLE);
            binding.imageviewDocumentForward.setVisibility(View.INVISIBLE);
            binding.textviewDocumentAudioEndTime.setText("다운로드 실패");
            binding.seekbarAudioProgress.setEnabled(false);

            isDownloadFailed = true;
        });
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
    public String getFolderId() {
        return folderId;
    }
    public String getRecordId() {
        return recordId;
    }
    public DocsDetailAdapter getDetailAdapter() {
        return detailAdapter;
    }
    public DocsSummaryAdapter getSummaryAdapter() {
        return summaryAdapter;
    }
    public void setBinding(ActivityDocsBinding binding) {
        this.binding = binding;
    }
    public void clearDisposable() {
        if (detailAdapter != null) {
            detailAdapter.clearDisposable();
        }
    }
}