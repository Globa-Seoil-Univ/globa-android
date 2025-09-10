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
    final public MutableLiveData<Boolean> isSearchResultEmpty = new MutableLiveData<>(false);

    private final SearchDocsAdapter adapter;
    private RecordDB recordDB;
    private RecordApiClient apiClient;

    public SearchViewModel() {
        adapter = new SearchDocsAdapter(new ArrayList<>(), this::deleteHistoryItem);
    }

    public void setContext(Context context) {
        recordDB = new RecordDB(context);
        apiClient = new RecordApiClient();
    }

    public SearchDocsAdapter getAdapter() {
        updateLocalSearchResults();
        return adapter;
    }

    private void deleteHistoryItem(SearchDocsItem item) {
        if (recordDB != null) {
            recordDB.deleteRecordById(item.getRecordId());
            updateLocalSearchResults();
        }
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

        if (response != null && response.getRecords() != null) {
            List<Record> records = response.getRecords();
            for (Record record : records) {
                SearchDocsItem item = new SearchDocsItem(record.getFolderId(), record.getRecordId(), record.getTitle(), record.getCreatedTime());
                docsItems.add(item);
            }
        }
        return docsItems;
    }

    public void updateLocalSearchResults() {
        if (recordDB == null) return;
        List<Record> records = recordDB.onSearch(searchText.getValue());
        ArrayList<SearchDocsItem> docsItems = new ArrayList<>();
        if (records != null) {
            for (Record record : records) {
                SearchDocsItem item = new SearchDocsItem(record.getFolderId(), record.getRecordId(), record.getTitle(), record.getCreatedTime());
                docsItems.add(item);
            }
        }
        updateRecyclerView(docsItems);
    }

    private void updateRecyclerView(ArrayList<SearchDocsItem> items) {
        adapter.setItems(items);
        isSearchResultEmpty.setValue(items.isEmpty());
    }

    public void onCancelClick() {
        isCancelClick.setValue(true);
    }
}