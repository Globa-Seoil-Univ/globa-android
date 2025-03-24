package team.y2k2.globa.docs.statistics;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.response.StatisticsResponse;

public class DocsStatisticsActivityModel extends ViewModel {
    private final MutableLiveData<StatisticsResponse> docsStatisticsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private RecordApiClient apiClient;

    public void setApiClient(Context context) {
        apiClient = new RecordApiClient();
    }

    public LiveData<StatisticsResponse> getDocsStatisticsLiveData() {
        return docsStatisticsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getDocsStatistics(String folderId, String recordId) {
        StatisticsResponse response = apiClient.requestDocsStatistics(folderId, recordId);
        docsStatisticsLiveData.postValue(response);
    }

}
