package team.y2k2.globa.main.folder.move;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityFolderMoveBinding;

public class FolderMoveActivity extends AppCompatActivity {

    ActivityFolderMoveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFolderMoveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("기본 폴더");
        itemList.add("2024 회의록");
        itemList.add("2023 회의록");

        binding.groupFolderMoveRadioGroup.removeAllViews();
        for (String item : itemList) {
            RadioButton radioButton = new RadioButton(FolderMoveActivity.this);
            radioButton.setText(item);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            radioButton.setBackgroundResource(R.drawable.folder_move_radio_button);
            binding.groupFolderMoveRadioGroup.addView(radioButton);
        }

        binding.groupFolderMoveRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // 라디오 버튼 클릭시 작동
        });

    }
}