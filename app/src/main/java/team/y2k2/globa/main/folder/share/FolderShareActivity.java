package team.y2k2.globa.main.folder.share;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderShareBinding;

public class FolderShareActivity extends AppCompatActivity {

    ActivityFolderShareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String input = binding.edittextFoldershareInputname.getText().toString();
        if(input.equals("")) {
            binding.buttonFoldershareCancel.setWidth(0);
        }

        binding.layoutFoldershareDropbox.setOnClickListener(v -> {
            showAlertDialog();
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_foldershare_authority, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}