package team.y2k2.globa.theme;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityThemeBinding;

public class ThemeActivity extends AppCompatActivity {

    Boolean lightOn = true, darkOn = false, systemOn = false;
    ActivityThemeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonThemeLightcheck.setOnClickListener(v -> {
            if(lightOn) {
                binding.buttonThemeLightcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            } else {
                binding.buttonThemeLightcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            }
        });

        binding.buttonThemeDarkcheck.setOnClickListener(v -> {
            if(darkOn) {
                binding.buttonThemeDarkcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            } else {
                binding.buttonThemeDarkcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            }
        });

        binding.buttonThemeSystemcheck.setOnClickListener(v -> {
            if(systemOn) {
                binding.buttonThemeSystemcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            } else {
                binding.buttonThemeSystemcheck.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            }
        });

    }
}