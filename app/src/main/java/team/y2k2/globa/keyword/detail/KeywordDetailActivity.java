package team.y2k2.globa.keyword.detail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import team.y2k2.globa.databinding.ActivityKeywordDetailBinding;

public class KeywordDetailActivity extends AppCompatActivity {

    ActivityKeywordDetailBinding binding;

    KeywordDetailModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKeywordDetailBinding.inflate(getLayoutInflater());

        model = new KeywordDetailModel();

        KeywordDetailItemAdapter adapter = new KeywordDetailItemAdapter(model.getItems());

        binding.recyclerviewKeyword.setAdapter(adapter);
        binding.recyclerviewKeyword.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        setContentView(binding.getRoot());
    }
}
