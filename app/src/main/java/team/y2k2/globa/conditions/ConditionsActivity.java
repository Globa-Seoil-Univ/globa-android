package team.y2k2.globa.conditions;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.databinding.ActivityConditionsBinding;

public class ConditionsActivity extends AppCompatActivity {

    private ActivityConditionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConditionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}