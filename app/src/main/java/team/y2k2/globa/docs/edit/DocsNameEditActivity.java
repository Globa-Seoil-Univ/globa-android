package team.y2k2.globa.docs.edit;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityDocsNameEditBinding;

public class DocsNameEditActivity extends AppCompatActivity {
    ActivityDocsNameEditBinding binding;

    String title;
    String recordId;
    String folderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsNameEditBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        recordId = intent.getStringExtra("recordId");
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");

        Log.d(getClass().getName(), recordId + " | " + folderId + " | " + title);

        binding.edittextFolderNameInputname.setHint(title);

        binding.textviewFolderNameChangeConfirm.setOnClickListener(v -> {
            String newName = binding.edittextFolderNameInputname.getText().toString();
            updateDocsName(newName);
        });

        setContentView(binding.getRoot());
    }

    public void updateDocsName(String title) {
        ApiClient apiClient = new ApiClient(this);
        apiClient.requestUpdateRecordName(folderId,recordId, title);
        finish();
    }
}
