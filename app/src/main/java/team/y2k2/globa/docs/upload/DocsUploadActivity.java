package team.y2k2.globa.docs.upload;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;

public class DocsUploadActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    String recordName;
    ActivityDocsUploadBinding binding;
    private static boolean isAudioPlayed;

    DocsUploadViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsUploadBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(DocsUploadViewModel.class);
        viewModel.setActivity(this);

        setTextViewTitles();
        setOnClickListeners();

        viewModel.loadFolder();
        setContentView(binding.getRoot());
    }


    public void setTextViewTitles() {
        binding.textviewDocsAudioTitle.setText(viewModel.getRecordName());
        binding.edittextDocsUploadTitle.setHint(viewModel.getRecordName());
    }

    public void setOnClickListeners() {
        binding.imagebuttonDocumentPlay.setOnClickListener(v -> {
            if(isAudioPlayed) {
                releaseMediaPlayer();
                binding.imagebuttonDocumentPlay.setImageResource(R.drawable.docs_play);
                isAudioPlayed = false;
                return;
            }

            binding.imagebuttonDocumentPlay.setImageResource(R.drawable.docs_pause);
            isAudioPlayed = true;
            playAudio(viewModel.getRecordPath());
        });

        binding.linearlayoutDocsUploadConfirm.setOnClickListener(v -> {
            viewModel.docsUpload();
        });
    }

    private void playAudio(String audioPath) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(audioPath));
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(v -> {
                releaseMediaPlayer();
            });
        } catch (Exception e) {
            Toast.makeText(this, "오류 발생" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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
        releaseMediaPlayer();
    }
}
