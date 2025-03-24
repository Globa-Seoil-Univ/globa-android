package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import java.util.List;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.services.InquiryApiService;
import team.y2k2.globa.api.services.NoticeApiService;

public class NoticeApiClient extends ApiClient {
    private NoticeApiService apiService;

    public NoticeApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(NoticeApiService.class);
    }

    public List<NoticeResponse> requestPromotion(int count) {
        return executeApiCall(apiService.requestPromotion(APPLICATION_JSON, getAuthorization(), count));
    }
}
