package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;
import team.y2k2.globa.api.services.AnswerApiService;
import team.y2k2.globa.api.services.DictionaryApiService;

public class DictionaryApiClient extends ApiClient {
    private DictionaryApiService apiService;

    public DictionaryApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(DictionaryApiService.class);
    }

    // 단어 검색
    public KeywordDetailResponse searchDictionary(String keyword) {
        return executeApiCall(apiService.searchDictionary(APPLICATION_JSON, getAuthorization(), keyword));
    }
}
