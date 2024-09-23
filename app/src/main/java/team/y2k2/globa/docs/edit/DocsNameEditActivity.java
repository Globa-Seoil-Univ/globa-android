package team.y2k2.globa.docs.edit;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
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

        binding.edittextDocsNameInputname.setText(title);

        binding.textviewDocsNameChangeConfirm.setOnClickListener(v -> {
            String newName = binding.edittextDocsNameInputname.getText().toString();
            if(binding.edittextDocsNameInputname.getText().toString().length() == 0) {
                Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateDocsName(newName);
        });

        binding.buttonDocsNameCancel.setOnClickListener(v -> {
            binding.edittextDocsNameInputname.setText("");
        });

        binding.edittextDocsNameInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewDocsNameCount.setText(s.length() + "/32");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textviewDocsNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                binding.textviewDocsNameCount.setText(s.length() + "/32");
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 32) {
                    binding.edittextDocsNameInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextDocsNameInputname.setText(text);
                    binding.edittextDocsNameInputname.setSelection(text.length());
                    binding.edittextDocsNameInputname.addTextChangedListener(this);
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

        setContentView(binding.getRoot());
    }

    public void updateDocsName(String title) {
        ApiClient apiClient = new ApiClient(this);
        apiClient.requestUpdateRecordName(folderId, recordId, title);
        Log.d(getClass().getName(), "folderId = " + folderId +", recordId = " + recordId + ", title =" + title);
        finish();
    }
}
