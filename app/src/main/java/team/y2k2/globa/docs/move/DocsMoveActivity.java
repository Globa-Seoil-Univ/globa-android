package team.y2k2.globa.docs.move;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.databinding.ActivityDocsMoveBinding;
import team.y2k2.globa.docs.upload.DocsUploadFolderAdapter;
import team.y2k2.globa.api.model.response.FolderResponse;

public class DocsMoveActivity extends AppCompatActivity {
    ActivityDocsMoveBinding binding;
    String recordId;
    String title;
    String folderId;

    List<FolderResponse> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoveBinding.inflate(getLayoutInflater());

        setBundleParams();
        binding.textviewDocsAudioTitle.setText(title);
        setOnClickListeners();
        loadFolder();

        setContentView(binding.getRoot());
    }

    public void loadFolder() {
        ApiClient apiClient = new ApiClient(this);
        List<FolderResponse> responses = apiClient.requestGetFolders(1, 10);

        if (responses != null) {
            DocsUploadFolderAdapter adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, responses);
            adapter.setDropDownViewResource(R.layout.item_folder);
            binding.spinnerDocsMove.setAdapter(adapter);
            binding.spinnerDocsMove.setSelection(0);
        }
    }

    public void moveDocs() {
        FolderResponse response = responses.get(binding.spinnerDocsMove.getSelectedItemPosition());
        String targetFolderId = String.valueOf(response.getFolderId());

        ApiClient apiClient = new ApiClient(this);
        apiClient.requestUpdateDocsMove(folderId, recordId, targetFolderId);
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
