package team.y2k2.globa.theme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityThemeBinding;

public class ThemeActivity extends AppCompatActivity {

    ActivityThemeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.radiogroupThemeButtongroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobutton_theme_lightmode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.radiobutton_theme_darkmode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if(checkedId == R.id.radiobutton_theme_system) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        binding.buttonThemeBack.setOnClickListener(v -> finish());

    }
}