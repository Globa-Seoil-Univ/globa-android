package team.y2k2.globa.main.profile.edit;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNicknameEditBinding;

public class NicknameEditActivity extends AppCompatActivity {

    ActivityNicknameEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNicknameEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonNicknameeditBack.setOnClickListener(v -> {
            finish();
        });

        binding.buttonNicknameeditCancel.setOnClickListener(v -> {
            binding.edittextNicknameeditInputname.setText("");
        });
    }
}