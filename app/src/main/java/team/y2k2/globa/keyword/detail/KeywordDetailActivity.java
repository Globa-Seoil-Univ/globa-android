package team.y2k2.globa.keyword.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import team.y2k2.globa.R;

public class KeywordDetailActivity extends AppCompatActivity {
    private KeywordDetailViewModel viewModel;
    private TextView pronunciationTextView;
    private RecyclerView recyclerView;
    private KeywordDetailAdapter adapter;
    private ProgressBar progressBar;
    private TextView noKeywordsTextView;
    private TextView wordTextView;
    private ImageView backButton;

    private TextView sourceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_detail);

        setupUI();
        setupViewModel();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(KeywordDetailViewModel.class);

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) {
            wordTextView.setText(keyword);
            viewModel.searchDictionary(keyword);
        }
    }

    private void setupUI() {
        wordTextView = findViewById(R.id.textview_keyword_detail_word);
        pronunciationTextView = findViewById(R.id.textview_keyword_detail_pronunciation);
        recyclerView = findViewById(R.id.recyclerview_keyword);
        progressBar = findViewById(R.id.progress_bar);
        noKeywordsTextView = findViewById(R.id.textview_no_keywords);
        backButton = findViewById(R.id.imageview_keyword_detail_top);
        sourceTextView = findViewById(R.id.textview_keyword_detail_source);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KeywordDetailAdapter(this);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.getPronunciation().observe(this, pronunciation -> {
            if (pronunciation != null) {
                pronunciationTextView.setText(pronunciation);
            }
        });

        viewModel.getKeywordItems().observe(this, items -> {
            if (items != null) {
                adapter.setItems(items);
            }
        });

        viewModel.getIsListEmpty().observe(this, isEmpty -> {
            if (isEmpty) {
                recyclerView.setVisibility(View.GONE);
                noKeywordsTextView.setVisibility(View.VISIBLE);
                sourceTextView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noKeywordsTextView.setVisibility(View.GONE);
                sourceTextView.setVisibility(View.VISIBLE);
            }
        });



        viewModel.getIsLoading().observe(this, isLoading -> progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}