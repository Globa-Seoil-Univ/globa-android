package team.y2k2.globa.docs.edit;

import static team.y2k2.globa.api.ApiService.API_BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.R;
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
            if(binding.edittextFolderNameInputname.getText().toString().length() == 0) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateDocsName(newName);
        });

        binding.buttonFolderNameCancel.setOnClickListener(v -> {
            binding.edittextFolderNameInputname.setText("");
        });

        binding.edittextFolderNameInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 32) {
                    binding.edittextFolderNameInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextFolderNameInputname.setText(text);
                    binding.edittextFolderNameInputname.setSelection(text.length());
                    binding.edittextFolderNameInputname.addTextChangedListener(this);
                }

                if (s.length() <= 32) {
                    binding.textviewFolderNameCount.setText(s.length() + "/32");
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }

                if (s.length() == 0) {
                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

        setContentView(binding.getRoot());
    }

    public void updateDocsName(String title) {
        ApiClient apiClient = new ApiClient(this);
        apiClient.requestUpdateRecordName(folderId, recordId, title);
        finish();
    }
}
