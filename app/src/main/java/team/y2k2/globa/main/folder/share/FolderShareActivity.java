package team.y2k2.globa.main.folder.share;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

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

        RelativeLayout readButton = dialogView.findViewById(R.id.relativelayout_foldershare_read);
        RelativeLayout wirteButton = dialogView.findViewById(R.id.relativelayout_foldershare_write);

        AlertDialog dialog = builder.create();
        dialog.show();

        readButton.setOnClickListener(v -> {
            // 읽기 권한 클릭 시 작동
        });
        wirteButton.setOnClickListener(v -> {
            // 편집 권한 클릭 시 작동
        });
    }

}