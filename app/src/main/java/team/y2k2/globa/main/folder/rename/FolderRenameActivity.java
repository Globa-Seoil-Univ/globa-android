package team.y2k2.globa.main.folder.rename;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderRenameBinding;

public class FolderRenameActivity extends AppCompatActivity {

    ActivityFolderRenameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderRenameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("Key");
        binding.edittextFolderrenameInputname.setText(name);

        binding.buttonFolderrenameCancel.setOnClickListener(v -> {
            binding.edittextFolderrenameInputname.setText("");
        });

    }
}