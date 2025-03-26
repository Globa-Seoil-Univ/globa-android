package team.y2k2.globa.main.search;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.sql.RecordDB;

public class SearchViewModel extends ViewModel {

    final public MutableLiveData<String> searchText = new MutableLiveData<>("");
    final public MutableLiveData<Boolean> isCancelClick = new MutableLiveData<>();

    private final SearchDocsAdapter adapter;
    private RecordDB recordDB;
    private RecordApiClient apiClient;

    public SearchViewModel() {
        adapter = new SearchDocsAdapter(new ArrayList<>());
    }

    public void setContext(Context context) {
        recordDB = new RecordDB(context);
        apiClient = new RecordApiClient();
    }

    public SearchDocsAdapter getAdapter() {
        performSearch();
        return adapter;
    }

    public void performSearch() {
        String keyword = searchText.getValue();
        if (keyword != null && !keyword.isEmpty()) {
            ArrayList<SearchDocsItem> apiResults = getRecordSearchResultToAPI(keyword);
            updateRecyclerView(apiResults);
        } else {
            updateLocalSearchResults();
        }
    }

    private ArrayList<SearchDocsItem> getRecordSearchResultToAPI(String keyword) {
        ArrayList<SearchDocsItem> docsItems = new ArrayList<>();
        SearchResponse response = apiClient.searchForKeyword(keyword);

        if (response.getRecords() != null) {
            List<Record> records = response.getRecords();
            for (Record record : records) {
                SearchDocsItem item = new SearchDocsItem(record.getFolderId(), record.getRecordId(), record.getTitle(), record.getCreatedTime());
                docsItems.add(item);
            }
        }
        return docsItems;
    }

    private void updateLocalSearchResults() {
        List<Record> records = recordDB.onSearch(searchText.getValue());
        ArrayList<SearchDocsItem> docsItems = new ArrayList<>();
        for (Record record : records) {
            SearchDocsItem item = new SearchDocsItem(record.getFolderId(), record.getRecordId(), record.getTitle(), record.getCreatedTime());
            docsItems.add(item);
        }
        updateRecyclerView(docsItems);
    }

    private void updateRecyclerView(ArrayList<SearchDocsItem> items) {
        adapter.setItems(items);
    }

    public void onCancelClick() {
        isCancelClick.setValue(true);
    }
}