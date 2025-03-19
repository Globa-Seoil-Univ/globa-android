package team.y2k2.globa.docs.upload;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;

public class DocsUploadActivity extends AppCompatActivity {
    public AlertDialog dialog;
    ActivityDocsUploadBinding binding;
    DocsUploadViewModel viewModel;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_docs_upload);
        viewModel = new ViewModelProvider(this).get(DocsUploadViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setActivity(this);
        viewModel.loadFolder();
        viewModel.loadLanguage();

        observeViewModel();
    }

    private void observeViewModel() {
        // 제목 옵저버
        viewModel.getDocsTitle().observe(this, title -> {
            binding.textviewDocsAudioTitle.setText(title);
            binding.edittextDocsUploadTitle.setHint(title);
        });

        // 재생 상태에 따라 UI 업데이트
        viewModel.getAudioPlayState().observe(this, playState -> {
            if (playState == AudioPlayState.PLAYING) {
                binding.imageButtonDocumentPlay.setImageResource(R.drawable.docs_pause);
            } else {
                binding.imageButtonDocumentPlay.setImageResource(R.drawable.docs_play);
            }
        });

        // 업로드 상태에 따른 UI 업데이트
        viewModel.getUploadStatus().observe(this, status -> {
            if (status.equals("LOADING")) {
                showLoadingDialog();
            } else {
                dismissLoadingDialog();
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                if (status.equals("파일 업로드 성공")) {
                    finish();
                }
            }
        });
    }

    private void showLoadingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.releaseMediaPlayer();
    }
}
