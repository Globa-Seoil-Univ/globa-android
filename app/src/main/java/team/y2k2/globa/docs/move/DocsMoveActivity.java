package team.y2k2.globa.docs.move;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityDocsMoveBinding;
import team.y2k2.globa.docs.upload.DocsUploadFolderAdapter;

public class DocsMoveActivity extends AppCompatActivity {
    ActivityDocsMoveBinding binding;
    DocsMoveModel viewModel;
    String recordId;
    String title;
    String folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoveBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(DocsMoveModel.class);
        viewModel.setApiClient(this);

        setBundleParams();
        binding.textviewDocsAudioTitle.setText(title);
        observeViewModel();
        viewModel.loadFolders();
        setOnClickListeners();

        setContentView(binding.getRoot());
    }

    private void observeViewModel() {
        viewModel.getFolderResponseLiveData().observe(this, folderResponse -> {
            if (folderResponse != null) {
                DocsUploadFolderAdapter adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, folderResponse.getFolders());
                adapter.setDropDownViewResource(R.layout.item_folder);
                binding.spinnerDocsMove.setAdapter(adapter);
                binding.spinnerDocsMove.setSelection(0);
            } else {
                Toast.makeText(this, "폴더 목록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getMoveSuccessLiveData().observe(this, success -> {
            if (success) {
                finish();
            } else {
                Toast.makeText(this, "폴더 이동 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsMoving().observe(this, isMoving -> {
            if (isMoving) {
                binding.progressbarMoveLoading.setVisibility(View.VISIBLE);
                binding.linearlayoutDocsMoveConfirm.setEnabled(false); // 버튼 비활성화
                binding.linearlayoutDocsMoveConfirm.setAlpha(0.5f); // 버튼을 반투명하게 처리
            } else {
                binding.progressbarMoveLoading.setVisibility(View.GONE);
                binding.linearlayoutDocsMoveConfirm.setEnabled(true); // 버튼 다시 활성화
                binding.linearlayoutDocsMoveConfirm.setAlpha(1.0f); // 버튼 투명도 복원
            }
        });
    }

    private void setOnClickListeners() {
        binding.imageButtonDocsMoveBack.setOnClickListener(v -> finish());
        binding.linearlayoutDocsMoveConfirm.setOnClickListener(v -> {
            int selectedFolderPosition = binding.spinnerDocsMove.getSelectedItemPosition();
            viewModel.moveDocs(folderId, recordId, selectedFolderPosition, viewModel.getFolderResponseLiveData().getValue());
        });
    }

    private void setBundleParams() {
        Intent intent = getIntent();
        recordId = intent.getStringExtra("recordId");
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");
    }
}