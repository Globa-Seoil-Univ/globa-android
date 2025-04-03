package team.y2k2.globa.keyword.detail;

import android.app.Activity;

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

    private DictionaryApiClient apiClient;

    public void setApiClient(Activity activity) {
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
        if (response != null && !response.getDictionary().isEmpty()) {
            List<KeywordDetail> keywordDetailList = response.getDictionary();
            
            if (!keywordDetailList.isEmpty()) {
                pronunciation.setValue(keywordDetailList.get(0).getPronunciation());
            } else {
                pronunciation.setValue("입력된 정보가 없습니다.");
            }

            List<KeywordDetailItem> items = new ArrayList<>();
            for (KeywordDetail keywordDetail : keywordDetailList) {
                items.add(new KeywordDetailItem(
                    keywordDetail.getWord(),
                    keywordDetail.getDescription(),
                    keywordDetail.getCategory()
                ));
            }
            keywordItems.setValue(items);
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
}
