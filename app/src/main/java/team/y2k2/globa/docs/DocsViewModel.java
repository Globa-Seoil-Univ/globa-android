package team.y2k2.globa.docs;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.databinding.ActivityDocsBinding;
import team.y2k2.globa.docs.detail.DocsDetailAdapter;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.summary.DocsSummaryAdapter;
import team.y2k2.globa.docs.summary.DocsSummaryModel;

public class DocsViewModel extends ViewModel {

    DocsActivity activity;
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
    private SimpleExoPlayer player;

    public void setActivity(DocsActivity activity) {
        this.activity = activity;
    }

    public void setIntent(Intent intent){
        title = intent.getStringExtra("title").toString();
        folderId = intent.getStringExtra("folderId").toString();
        recordId = intent.getStringExtra("recordId").toString();
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    public void getResponse() {
        ApiClient apiClient = new ApiClient(activity);

        DocsDetailResponse response = apiClient.requestGetDocumentDetail(folderId, recordId);

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
                                activity.startUpdatingSeekBar();

                            } else {
                                isMusicStarted = false;
                                player.pause();
                                binding.imagebuttonDocumentAudioPlay.setImageResource(R.drawable.docs_play);
                                activity.stopUpdatingSeekBar();
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
                .addOnFailureListener(e-> Log.e("FirebaseStorage", "다운로드 URL 가져오기 실패", e));
    }

    public String getTitle() {
        return title;
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

    public void clearDisposable() {

        detailAdapter.clearDisposable();

    }

}
