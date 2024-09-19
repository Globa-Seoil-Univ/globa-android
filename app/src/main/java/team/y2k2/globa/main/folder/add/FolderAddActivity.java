package team.y2k2.globa.main.folder.add;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.R;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.databinding.ActivityFolderAddBinding;

public class FolderAddActivity extends AppCompatActivity {

    ActivityFolderAddBinding binding;
    ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiClient = new ApiClient(this);

        binding.buttonFolderaddBack.setOnClickListener(v -> {
            finish();
        });

        binding.textviewFolderAddConfirm.setOnClickListener(v -> {

            if(binding.edittextFolderaddInputname.getText().length() == 0) {
                Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = binding.edittextFolderaddInputname.getText().toString();
            apiClient.requestInsertFolder(title);
            finish();

        });

        binding.edittextFolderaddInputname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.textviewFolderaddCount.setText(s.length() + "/32");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    binding.textviewFolderaddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                } else {
                    binding.textviewFolderaddCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 32) {
                    binding.edittextFolderaddInputname.removeTextChangedListener(this);
                    String text = s.toString().substring(0, 32);
                    binding.edittextFolderaddInputname.setText(text);
                    binding.edittextFolderaddInputname.setSelection(text.length());
                    binding.edittextFolderaddInputname.addTextChangedListener(this);
                }

                if(s.length() <= 32) {
                    binding.textviewFolderaddCount.setText(s.length() + "/32");
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                }

                if(s.length() == 0) {
                    binding.textviewFolderAddConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }
        });

    }
}