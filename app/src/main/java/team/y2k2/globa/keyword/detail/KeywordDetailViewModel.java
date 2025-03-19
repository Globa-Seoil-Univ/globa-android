package team.y2k2.globa.keyword.detail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;

public class KeywordDetailViewModel extends ViewModel {

    private ApiClient apiClient;
    private final MutableLiveData<KeywordDetailResponse> keywordDetailResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        this.apiClient = new ApiClient(context);
    }

    public LiveData<KeywordDetailResponse> getKeywordDetailResponseLiveData() {
        return keywordDetailResponseLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void searchDictionary(String keyword) {
        KeywordDetailResponse response = apiClient.searchDictionary(keyword);
        keywordDetailResponseLiveData.setValue(response);
    }
}
