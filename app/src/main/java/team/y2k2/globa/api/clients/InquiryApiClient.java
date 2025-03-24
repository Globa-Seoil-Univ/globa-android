package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.InquiryRequest;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;
import team.y2k2.globa.api.services.InquiryApiService;

public class InquiryApiClient extends ApiClient {
    private InquiryApiService apiService;

    public InquiryApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(InquiryApiService.class);
    }

    // 문의 상세
    public InquiryDetailResponse requestGetInquiryDetail(String inquiryId) {
        return executeApiCall(apiService.requestGetInquiryDetail(inquiryId, APPLICATION_JSON, getAuthorization()));
    }

    // 문의 추가
    public Response<Void> requestInsertInquiry(String title, String content) {
        return executeVoidApiCall(apiService.requestInsertInquiry(APPLICATION_JSON, getAuthorization(), new InquiryRequest(title, content)));
    }
}
