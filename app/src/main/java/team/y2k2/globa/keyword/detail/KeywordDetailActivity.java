package team.y2k2.globa.keyword.detail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.KeywordDetail;
import team.y2k2.globa.databinding.ActivityKeywordDetailBinding;

public class KeywordDetailActivity extends AppCompatActivity {

    ActivityKeywordDetailBinding binding;
    KeywordDetailViewModel keywordDetailViewModel;
    String keyword;

    ArrayList<KeywordDetailItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKeywordDetailBinding.inflate(getLayoutInflater());

        keyword = getIntent().getStringExtra("keyword");

        keywordDetailViewModel = new ViewModelProvider(this).get(KeywordDetailViewModel.class);
        keywordDetailViewModel.searchKeyword(keyword);

        keywordDetailViewModel.getKeywordDetailResponseLiveData().observe(KeywordDetailActivity.this, response -> {
            if(response != null) {

                List<KeywordDetail> keywordDetailList = response.getDictionary();

                for(KeywordDetail keywordDetail : keywordDetailList) {
                    String word = keywordDetail.getWord();
                    String engWord = keywordDetail.getEngword();
                    String description = keywordDetail.getDescription();
                    String category = keywordDetail.getCategory();
                    String pronunciation = keywordDetail.getPronunciation();

                    binding.textviewKeywordDetailWord.setText(word);
                    binding.textviewKeywordDetailPronunciation.setText(pronunciation);

                    itemList.add(new KeywordDetailItem(word, description, category));
                }

                KeywordDetailItemAdapter adapter = new KeywordDetailItemAdapter(itemList);
                binding.recyclerviewKeyword.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                binding.recyclerviewKeyword.setAdapter(adapter);

            }
        });

        binding.imageviewKeywordDetailTop.setOnClickListener(v -> {
            finish();
        });

        setContentView(binding.getRoot());
    }
}
