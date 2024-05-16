package team.y2k2.globa.docs.move;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.databinding.ActivityDocsMoveBinding;
import team.y2k2.globa.docs.upload.DocsUploadFolderAdapter;
import team.y2k2.globa.main.folder.FolderResponse;

public class DocsMoveActivity extends AppCompatActivity {

    ActivityDocsMoveBinding binding;

    String recordId;
    String title;
    String folderId;


    Retrofit retrofit;
    SharedPreferences preferences;
    String authorization;
    ApiService apiService;

    List<FolderResponse> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoveBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        recordId = intent.getStringExtra("recordId");
        title = intent.getStringExtra("title");
        folderId = intent.getStringExtra("folderId");

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        authorization = "Bearer " + preferences.getString("accessToken", "");
        apiService = retrofit.create(ApiService.class);

        binding.textviewDocsAudioTitle.setText(title);

        loadFolder();

        binding.linearlayoutDocsMoveConfirm.setOnClickListener(v -> {
            moveDocs();
            finish();
        });

        setContentView(binding.getRoot());
    }


    public void loadFolder() {
        Call<List<FolderResponse>> call = apiService.requestGetFolders("application/json",authorization, 1, 10);
        call.enqueue(new Callback<List<FolderResponse>>() {
            @Override
            public void onResponse(Call<List<FolderResponse>> call, Response<List<FolderResponse>> response) {
                if (response.isSuccessful()) {
                    responses = response.body();
                    if (responses != null) {
                        DocsUploadFolderAdapter adapter = new DocsUploadFolderAdapter(getApplicationContext(), R.layout.item_folder, responses);
                        adapter.setDropDownViewResource(R.layout.item_folder);
                        binding.spinnerDocsMove.setAdapter(adapter);
                        binding.spinnerDocsMove.setSelection(0);
                    }
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d(getClass().getName(), "오류:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FolderResponse>> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d(getClass().getName(), "오류" + t.getMessage());
            }
        });
    }

    public void moveDocs() {
        FolderResponse response = responses.get(binding.spinnerDocsMove.getSelectedItemPosition());
        DocsMoveRequest request = new DocsMoveRequest(String.valueOf(response.getFolderId()));

        Log.d(getClass().getName(), "HEADER");
        Log.d(getClass().getName(), "folderId: " + folderId);
        Log.d(getClass().getName(), "recordId: " + recordId);
        Log.d(getClass().getName(), "BODY");
        Log.d(getClass().getName(), "targetId: " + request.getTargetId());

        Call<Void> call = apiService.requestUpdateDocsMove(folderId, recordId,"application/json",authorization, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d(getClass().getName(), "오류:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 요청 실패 시 처리
                Log.d(getClass().getName(), "오류" + t.getMessage());
            }
        });
    }
}
