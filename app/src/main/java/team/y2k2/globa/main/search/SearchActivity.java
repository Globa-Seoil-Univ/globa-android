package team.y2k2.globa.main.search;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import team.y2k2.globa.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.setContext(this);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setupRecyclerView();
        setupListeners();
    }

    private void setupRecyclerView() {
        binding.recyclerviewSearchHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewSearchHistory.setAdapter(viewModel.getAdapter());
    }

    private void setupListeners() {
        binding.textviewSearchCancel.setOnClickListener(v -> finish());
        binding.edittextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.performSearch();
                return true;
            }
            return false;
        });

        viewModel.isCancelClick.observe(this, shouldFinish -> {
            if (shouldFinish != null && shouldFinish) {
                finish();
            }
        });
    }
}