package team.y2k2.globa.main.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.databinding.ActivitySearchBinding;
import team.y2k2.globa.sql.RecordDB;

public class SearchActivity extends AppCompatActivity {

    RecordDB recordDB;
    ActivitySearchBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());

        binding.textviewSearchCancel.setOnClickListener(v -> {
            finish();
        });

        recordDB = new RecordDB(this);
        updateSearchResults("");


        binding.edittextSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        // 사용자가 확인 동작을 실행 시
        binding.edittextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    ArrayList<SearchDocsItem> docsItems = getRecordSearchResultToAPI(v.getText().toString());
                    SearchDocsAdapter adapter = new SearchDocsAdapter(docsItems);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                    binding.recyclerviewSearchHistory.setAdapter(adapter);
                    binding.recyclerviewSearchHistory.setLayoutManager(layoutManager);
                }
                else {
                    CharSequence s = binding.edittextSearch.getText();
                    List<Record> records = recordDB.onSearch(String.valueOf(s));
                    ArrayList<SearchDocsItem> docsItems = new ArrayList<>();

                    if(docsItems.size() != 0)
                        for(int i = 0; i < records.size(); i++) {
                            Record record = records.get(i);
                            String folderId = record.getFolderId();
                            String recordId = record.getRecordId();
                            String title = record.getTitle();
                            String datetime = record.getCreatedTime();

                            SearchDocsItem item = new SearchDocsItem(folderId, recordId, title, datetime);
                            docsItems.add(item);
                        }

                    Log.d(getClass().getName(), docsItems.size() + "개 확인됨");

                    SearchDocsAdapter adapter = new SearchDocsAdapter(docsItems);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                    binding.recyclerviewSearchHistory.setAdapter(adapter);
                    binding.recyclerviewSearchHistory.setLayoutManager(layoutManager);

                }

                return false;
            }
        });

        binding.edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchResults(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        setContentView(binding.getRoot());
    }

    private void updateSearchResults(CharSequence s) {
        List<Record> records = recordDB.onSearch(String.valueOf(s));
        ArrayList<SearchDocsItem> docsItems = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            String folderId = record.getFolderId();
            String recordId = record.getRecordId();
            String title = record.getTitle();
            String datetime = record.getCreatedTime();

            SearchDocsItem item = new SearchDocsItem(folderId, recordId, title, datetime);
            docsItems.add(item);
        }

        Log.d(getClass().getName(), docsItems.size() + "개 확인됨");

        SearchDocsAdapter adapter = new SearchDocsAdapter(docsItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.recyclerviewSearchHistory.setAdapter(adapter);
        binding.recyclerviewSearchHistory.setLayoutManager(layoutManager);
    }

    public ArrayList<SearchDocsItem> getRecordSearchResultToAPI(String keyword) {
        ArrayList<SearchDocsItem> docsItems = new ArrayList<>();

        ApiClient apiClient = new ApiClient(this);
        SearchResponse response = apiClient.searchForKeyword(keyword);

        if(response.getRecords() != null) {

            List<Record> records = response.getRecords();

            for(int i = 0; i < records.size(); i ++) {
                Record record = records.get(i);

                String recordId = record.getRecordId();
                String folderId = record.getFolderId();
                String title = record.getTitle();
                String datetime = record.getCreatedTime();

//            String userName = record.getUploaderList().get(0).getName();
//            String profile = record.getUploaderList().get(0).getProfile();
//            String userId = record.getUploaderList().get(0).getUserId();

                SearchDocsItem item = new SearchDocsItem(folderId, recordId, title, datetime);
                docsItems.add(item);

//            RecordDB recordDB = new RecordDB(this);
//            recordDB.onInsert(Integer.valueOf(recordId), Integer.valueOf(folderId), title, datetime);
            }

        }
        return docsItems;
    }


}
