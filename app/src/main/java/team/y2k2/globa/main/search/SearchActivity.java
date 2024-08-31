package team.y2k2.globa.main.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.databinding.ActivitySearchBinding;
import team.y2k2.globa.sql.RecordDB;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());

        binding.textviewSearchCancel.setOnClickListener(v -> {
            finish();
        });

        RecordDB recordDB = new RecordDB(this);

        binding.edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Record> records = recordDB.onSearch(String.valueOf(s));
                ArrayList<SearchDocsItem> docsItems = new ArrayList<>();

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

            @Override
            public void afterTextChanged(Editable s) {
//
//                if (s.length() > 32) {
//                    binding.edittextFolderNameInputname.removeTextChangedListener(this);
//                    String text = s.toString().substring(0, 32);
//                    binding.edittextFolderNameInputname.setText(text);
//                    binding.edittextFolderNameInputname.setSelection(text.length());
//                    binding.edittextFolderNameInputname.addTextChangedListener(this);
//                }
//
//                if (s.length() <= 32) {
//                    binding.textviewFolderNameCount.setText(s.length() + "/32");
//                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
//                }
//
//                if (s.length() == 0) {
//                    binding.textviewFolderNameChangeConfirm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
//                }
            }
        });

        setContentView(binding.getRoot());
    }
}
