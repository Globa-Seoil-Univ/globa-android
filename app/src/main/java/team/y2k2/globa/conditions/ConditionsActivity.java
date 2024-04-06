package team.y2k2.globa.conditions;

import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import team.y2k2.globa.R;
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