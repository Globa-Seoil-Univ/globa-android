package team.y2k2.globa.keyword.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import team.y2k2.globa.R;

public class KeywordDetailActivity extends AppCompatActivity {
    private KeywordDetailViewModel viewModel;
    private TextView titleTextView;
    private TextView pronunciationTextView;
    private RecyclerView recyclerView;
    private KeywordDetailAdapter adapter;
    private ProgressBar progressBar;
    private TextView noKeywordsTextView;
    private ImageView backButton;
    private TextView sourceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_keyword_detail);

        setupUI();
        setupViewModel();
        observeViewModel();

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary));

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(KeywordDetailViewModel.class);

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) {
            titleTextView.setText(keyword);
            viewModel.searchDictionary(keyword);
        }
    }

    private void setupUI() {
        titleTextView = findViewById(R.id.textview_keyword_detail_word);
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