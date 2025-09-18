package team.y2k2.globa.docs.upload;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsUploadBinding;

public class DocsUploadActivity extends AppCompatActivity {
    private AlertDialog loadingDialog;
    public ActivityDocsUploadBinding binding;
    private DocsUploadViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_docs_upload);
        viewModel = new ViewModelProvider(this).get(DocsUploadViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.setActivity(this);
        viewModel.loadFolder();
        viewModel.loadLanguage();

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        setupLoadingDialog();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getDocsTitle().observe(this, title -> {
            binding.textviewDocsAudioTitle.setText(title);
            binding.edittextDocsUploadTitle.setHint(title);
        });

        viewModel.getAudioPlayState().observe(this, playState -> {
            if (playState == AudioPlayState.PLAYING) {
                binding.imageButtonDocumentPlay.setImageResource(R.drawable.docs_pause);
            } else {
                binding.imageButtonDocumentPlay.setImageResource(R.drawable.docs_play);
            }
        });

        viewModel.getUploadStatus().observe(this, status -> {
            if (status == null) return;

            if (status.equals("LOADING")) {
                showLoadingDialog();
            } else {
                dismissLoadingDialog();
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                if (status.equals("파일 업로드 성공")) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void setupLoadingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        loadingDialog = builder.create();

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.releaseMediaPlayer();
        dismissLoadingDialog();
    }
}