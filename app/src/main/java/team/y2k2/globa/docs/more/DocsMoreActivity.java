package team.y2k2.globa.docs.more;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.databinding.ActivityDocsMoreBinding;

public class DocsMoreActivity extends AppCompatActivity {
    ActivityDocsMoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsMoreBinding.inflate(getLayoutInflater());

        binding.imagebuttonDocsMoreBack.setOnClickListener(v -> {
            finish();
        });


        setContentView(binding.getRoot());
    }
}
