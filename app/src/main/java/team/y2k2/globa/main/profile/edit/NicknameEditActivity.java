package team.y2k2.globa.main.profile.edit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNicknameEditBinding;

public class NicknameEditActivity extends AppCompatActivity {

    private ActivityNicknameEditBinding binding;
    private NicknameEditViewModel viewModel;
    private TextWatcher nicknameTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNicknameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NicknameEditViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        String userId = getIntent().getStringExtra("userId");
        String currentName = getIntent().getStringExtra("current_name");
        viewModel.initialize(userId, currentName);

        initViews(currentName);
        observeViewModel();
    }

    private void initViews(String currentName) {
        binding.textviewNicknameEditAnnounce.setText(getString(R.string.activity_nickname_edit_title));
        binding.textviewNicknameEditChange.setText(getString(R.string.change));
        binding.buttonNicknameEditBack.setBackgroundResource(R.drawable.header_back);
        binding.edittextNicknameEditInputName.setHint(currentName);
    }

    private void observeViewModel() {
        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_name", viewModel.getNewNickname());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        viewModel.getShowToast().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.doneShowToast();
            }
        });

        nicknameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전
                binding.textviewNicknameEditChange.setTextColor(Color.GRAY);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 변경 중
                viewModel.onNicknameChanged(s.toString());
                binding.textviewNicknameEditCount.setText(s.length() + "/32");
                binding.buttonNicknameEditCancel.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후
                viewModel.onNicknameAfterTextChanged(s.toString());
                if (s.length() > 32) {
                    binding.edittextNicknameEditInputName.removeTextChangedListener(nicknameTextWatcher);
                    String text = s.toString().substring(0, 32);
                    binding.edittextNicknameEditInputName.setText(text);
                    binding.edittextNicknameEditInputName.setSelection(text.length());
                    binding.edittextNicknameEditInputName.addTextChangedListener(nicknameTextWatcher);
                }
            }
        };

        binding.edittextNicknameEditInputName.addTextChangedListener(nicknameTextWatcher);

        viewModel.getIsChangeButtonEnabled().observe(this, isEnabled -> {
            if (isEnabled) {
                binding.textviewNicknameEditChange.setTextColor(ContextCompat.getColor(this, R.color.primary));
            } else {
                binding.textviewNicknameEditChange.setTextColor(ContextCompat.getColor(this, R.color.gray));
            }
        });
    }
}