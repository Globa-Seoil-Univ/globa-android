package team.y2k2.globa.main.folder.move;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderMoveBinding;
import team.y2k2.globa.main.MainActivity;

public class FolderMoveActivity extends AppCompatActivity {

    ActivityFolderMoveBinding binding;
    private ArrayList<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderMoveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemList = new ArrayList<>();
        itemList.add("기본 폴더");
        itemList.add("2024 회의록");
        itemList.add("2023 회의록");

        binding.groupFoldermoveRediogroup.removeAllViews();
        for(String item : itemList) {
            RadioButton radioButton = new RadioButton(FolderMoveActivity.this);
            radioButton.setText(item);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            radioButton.setBackgroundResource(R.drawable.folder_move_radio_button);
            binding.groupFoldermoveRediogroup.addView(radioButton);
        }

        binding.groupFoldermoveRediogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 라디오 버튼 클릭시 작동
            }
        });

    }
}