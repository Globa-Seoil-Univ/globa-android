package team.y2k2.globa.keyword.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.clients.DictionaryApiClient;
import team.y2k2.globa.api.model.entity.KeywordDetail;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;

public class KeywordDetailViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<KeywordDetailResponse> keywordDetailResponse = new MutableLiveData<>(null);
    private final MutableLiveData<String> pronunciation = new MutableLiveData<>(null);
    private final MutableLiveData<List<KeywordDetailItem>> keywordItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isListEmpty = new MutableLiveData<>(false);

    private final DictionaryApiClient apiClient;

    public KeywordDetailViewModel() {
        this.apiClient = new DictionaryApiClient();
    }

    public void searchDictionary(String keyword) {
        isLoading.setValue(true);
        KeywordDetailResponse response = apiClient.searchDictionary(keyword);
        keywordDetailResponse.setValue(response);
        processKeywordResponse(response);
        isLoading.setValue(false);
    }

    private void processKeywordResponse(KeywordDetailResponse response) {
        if (response != null && response.getDictionary() != null && !response.getDictionary().isEmpty()) {
            List<KeywordDetail> keywordDetailList = response.getDictionary();
            pronunciation.setValue(keywordDetailList.get(0).getPronunciation());

            List<KeywordDetailItem> items = new ArrayList<>();
            for (KeywordDetail keywordDetail : keywordDetailList) {
                items.add(new KeywordDetailItem(
                        keywordDetail.getWord(),
                        keywordDetail.getDescription(),
                        keywordDetail.getCategory()
                ));
            }
            keywordItems.setValue(items);
            isListEmpty.setValue(items.isEmpty());
        } else {
            keywordItems.setValue(new ArrayList<>());
            isListEmpty.setValue(true);
            pronunciation.setValue("");
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<KeywordDetailResponse> getKeywordDetailResponse() {
        return keywordDetailResponse;
    }

    public MutableLiveData<String> getPronunciation() {
        return pronunciation;
    }

    public MutableLiveData<List<KeywordDetailItem>> getKeywordItems() {
        return keywordItems;
    }

    public MutableLiveData<Boolean> getIsListEmpty() {
        return isListEmpty;
    }
}