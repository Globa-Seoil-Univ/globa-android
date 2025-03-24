package team.y2k2.globa.api.clients;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.services.AnswerApiService;

public class AnswerApiClient extends ApiClient {
    AnswerApiService apiService;

    AnswerApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(AnswerApiService.class);
    }
}
