package team.y2k2.globa.docs.edit;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import retrofit2.Response;
import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.RecordApiClient;
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

        binding.edittextDocsNameInputName.setText(title);

        binding.textviewDocsNameChangeConfirm.setOnClickListener(v -> {
            String newName = binding.edittextDocsNameInputName.getText().toString();
            if (binding.edittextDocsNameInputName.getText().toString().isEmpty()) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateDocsName(newName);
        });

        binding.buttonDocsNameCancel.setOnClickListener(v -> binding.edittextDocsNameInputName.setText(""));

        binding.textviewDocsNameCount.setText(title.length() + "/32");

        binding.edittextDocsNameInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewDocsNameCount.setText(s.length() + "/32");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textviewDocsNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.textviewDocsNameCount.setText(s.length() + "/32");
                if (s.length() == 0) {
                    binding.buttonDocsNameCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonDocsNameCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 32) {
                    binding.edittextDocsNameInputName.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextDocsNameInputName.setText(text);
                    binding.edittextDocsNameInputName.setSelection(text.length());
                    binding.edittextDocsNameInputName.addTextChangedListener(this);
                }

                if (s.length() <= 32) {
                    binding.textviewDocsNameCount.setText(s.length() + "/32");
                    binding.textviewDocsNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }

                if (s.length() == 0) {
                    binding.textviewDocsNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

        binding.buttonDocsNameBack.setOnClickListener(v -> finish());

        setContentView(binding.getRoot());
    }

    public void updateDocsName(String title) {
        RecordApiClient apiClient = new RecordApiClient();

        Response<Void> response = apiClient.requestUpdateRecordName(folderId, recordId, title);

        if (response != null && response.isSuccessful()) {
            Log.d(getClass().getName(), "folderId = " + folderId + ", recordId = " + recordId + ", title =" + title);
            finish();
        }
    }
}
