package team.y2k2.globa.keyword.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.KeywordDetail;
import team.y2k2.globa.databinding.ActivityKeywordDetailBinding;

public class KeywordDetailActivity extends AppCompatActivity {

    ActivityKeywordDetailBinding binding;
    KeywordDetailViewModel keywordDetailViewModel;
    KeywordDetailAdapter adapter;

    String keyword;

    ArrayList<KeywordDetailItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        setContentView(binding.getRoot());
    }

    public void initializeUI() {
        binding = ActivityKeywordDetailBinding.inflate(getLayoutInflater());

        keyword = getIntent().getStringExtra("keyword");

        binding.textviewKeywordDetailWord.setText(keyword);

        loadAPIResponse();

        binding.imageviewKeywordDetailTop.setOnClickListener(v -> {
            finish();
        });
    }

    public void loadAPIResponse() {
        keywordDetailViewModel = new ViewModelProvider(this).get(KeywordDetailViewModel.class);
        keywordDetailViewModel.searchKeyword(keyword);

        keywordDetailViewModel.getKeywordDetailResponseLiveData().observe(KeywordDetailActivity.this, response -> {
            if(response != null) {

                List<KeywordDetail> keywordDetailList = response.getDictionary();

                for(KeywordDetail keywordDetail : keywordDetailList) {

                    addItem(keywordDetail);

                }

                adapter = new KeywordDetailAdapter(itemList, this);
                binding.recyclerviewKeyword.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                binding.recyclerviewKeyword.setAdapter(adapter);

                if(itemList.isEmpty()) {
                    Log.d("키워드", "키워드 정보가 없음");
                    Toast.makeText(this, "검색된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("키워드", "키워드 정보가 있음 크기 : " + itemList.size());
                }

            }
        });
    }

    public void addItem(KeywordDetail keywordDetail) {
        String word = keywordDetail.getWord();
        String engWord = keywordDetail.getEngword();
        String description = keywordDetail.getDescription();
        String category = keywordDetail.getCategory();
        String pronunciation = keywordDetail.getPronunciation();

        binding.textviewKeywordDetailPronunciation.setText(pronunciation);

        Log.d("키워드", "word: " + word + ", engWord: " + engWord + ", description: " + description + ", category: " + category +
                ", pronunciation: " + pronunciation);

        itemList.add(new KeywordDetailItem(word, description, category));
    }

}
