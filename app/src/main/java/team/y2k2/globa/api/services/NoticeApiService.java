package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.NoticeApiEndpoint.GET_NOTICE;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.response.NoticeResponse;

public interface NoticeApiService {
//    @GET(GET_NOTICE_DETAIL); 공지사항 상세 조회

    /**
     * 간단 공지사항 조회
     */
    @GET(GET_NOTICE)
    Call<List<NoticeResponse>> requestPromotion(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("count") int count);
//    @POST(POST_NOTICE); 공지사항 추가

}
