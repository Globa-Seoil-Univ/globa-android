package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.InquiryApiEndpoint.GET_INQUIRY;
import static team.y2k2.globa.api.endpoints.InquiryApiEndpoint.GET_INQUIRY_DETAIL;
import static team.y2k2.globa.api.endpoints.InquiryApiEndpoint.POST_INQUIRY;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.InquiryRequest;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;
import team.y2k2.globa.api.model.response.NotificationInquiryResponse;

public interface InquiryApiService {
    /**
     * 문의 조회 TODO - 작업 필요
     */
    @GET(GET_INQUIRY)
    Call<NotificationInquiryResponse> requestGetInquires(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count, @Query("sort") String sort);

    /**
     * 문의 상세 조회
     */
    @GET(GET_INQUIRY_DETAIL)
    Call<InquiryDetailResponse> requestGetInquiryDetail(@Path("inquiry_id") String inquiryId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 문의 등록
     */
    @POST(POST_INQUIRY)
    Call<Void> requestInsertInquiry(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body InquiryRequest inquiryRequest);

}
