package team.y2k2.globa.keyword.detail;

import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_detail);

        setupViewModel();
        setupUI();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(KeywordDetailViewModel.class);
        viewModel.setApiClient(this);
        
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) {
            viewModel.searchDictionary(keyword);
        }
    }

    private void setupUI() {
        pronunciationTextView = findViewById(R.id.textview_keyword_detail_pronunciation);
        recyclerView = findViewById(R.id.recyclerview_keyword);
        progressBar = findViewById(R.id.progress_bar);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KeywordDetailAdapter(this);
        recyclerView.setAdapter(adapter);
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
                if (items.isEmpty()) {
                    Toast.makeText(this, "검색된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }
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
