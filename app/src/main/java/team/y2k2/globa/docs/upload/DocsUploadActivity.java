package team.y2k2.globa.docs.upload;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;

public class DocsUploadActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ActivityDocsUploadBinding binding;
    private static boolean isAudioPlayed;
    DocsUploadViewModel viewModel;

    AlertDialog.Builder builder;
    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsUploadBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(DocsUploadViewModel.class);
        viewModel.setActivity(this);

        setTextViewTitles();
        setOnClickListeners();

        viewModel.loadFolder();
        viewModel.loadLanguage();
        setContentView(binding.getRoot());
    }


    public void setTextViewTitles() {
        String docsTitle;
        if(viewModel.getRecordName().length() >= 20) {
            docsTitle = viewModel.getRecordName().substring(0, 20);
        }
        else
            docsTitle = viewModel.getRecordName();

        binding.textviewDocsAudioTitle.setText(docsTitle);
        binding.edittextDocsUploadTitle.setHint(docsTitle);
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
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);

            builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.show();
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
