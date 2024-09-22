package team.y2k2.globa.docs.move;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Folder;
import team.y2k2.globa.databinding.ActivityDocsMoveBinding;
import team.y2k2.globa.docs.upload.DocsUploadFolderAdapter;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsMoveActivity extends AppCompatActivity {
    ActivityDocsMoveBinding binding;
    String recordId;
    String title;
    String folderId;

    FolderResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoveBinding.inflate(getLayoutInflater());

        loadFolder();
        setBundleParams();
        binding.textviewDocsAudioTitle.setText(title);
        setOnClickListeners();

        setContentView(binding.getRoot());
    }

    public void loadFolder() {
        ApiClient apiClient = new ApiClient(this);
        response = apiClient.requestGetFolders(1, 100);

        if (response != null) {
            DocsUploadFolderAdapter adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, response.getFolders());
            adapter.setDropDownViewResource(R.layout.item_folder);
            binding.spinnerDocsMove.setAdapter(adapter);
            binding.spinnerDocsMove.setSelection(0);
        }
    }

    public void moveDocs() {
        if (response != null) { // null 체크 및 리스트가 비어있는지 확인
            Folder folder = response.getFolders().get(binding.spinnerDocsMove.getSelectedItemPosition());
            String targetFolderId = String.valueOf(folder.getFolderId());

            ApiClient apiClient = new ApiClient(this);
            apiClient.requestUpdateDocsMove(folderId, recordId, targetFolderId);
        } else {
            Toast.makeText(this, "폴더 목록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnClickListeners() {
        binding.imagebuttonDocsMoveBack.setOnClickListener(v -> {
            finish();
        });

        binding.linearlayoutDocsMoveConfirm.setOnClickListener(v -> {
            moveDocs();
            finish();
        });
    }

    public void setBundleParams() {
        Intent intent = getIntent();
        recordId = intent.getStringExtra("recordId");
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");
    }
}
