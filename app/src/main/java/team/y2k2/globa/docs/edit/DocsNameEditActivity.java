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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);


        SharedPreferences preferences = getSharedPreferences("account", Activity.MODE_PRIVATE);
        String authorization = "Bearer " + preferences.getString("accessToken", "");
        Log.d(getClass().getName(), "folderId : " + folderId);

        DocsNameEditRequest request = new DocsNameEditRequest(title);

        Call<Void> call = apiService.requestUpdateRecordName(folderId, recordId,"application/json", authorization, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    response.body();
                    // 성공적으로 응답을 받았을 때 처리
                    Log.d(getClass().getName(), "성공 : " + response.code());
                    finish();
                } else {
                    // 서버로부터 실패 응답을 받았을 때 처리
                    Log.d(getClass().getName(), "오류 : " + response.code());

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
