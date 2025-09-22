package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.UserApiEndpoint.DELETE_USER;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.GET_USER;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.GET_USER_ANALYSIS;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.GET_USER_NOTIFICATION;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.GET_USER_SEARCH;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.PATCH_USER_NAME;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.PATCH_USER_PROFILE;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.POST_USER;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.POST_USER_AUTH;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.POST_USER_NOTIFICATION_TOKEN;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.PUT_USER_NOTIFICATION;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.PUT_USER_NOTIFICATION_TOKEN;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.AlertRequest;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.request.NicknameEditRequest;
import team.y2k2.globa.api.model.request.NotificationTokenRequest;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.request.WithdrawRequest;
import team.y2k2.globa.api.model.response.AlertResponse;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.api.model.response.UserSearchResponse;

public interface UserApiService {
    /**
     * 회원 탈퇴
     * 1: 서비스 사용 불편, 2: 정확성 낮음, 3: 기능 부족, 4: 다른 서비스 선호
     */
    @DELETE(DELETE_USER)
    Call<Void> requestWithdrawUser(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body WithdrawRequest withdrawRequest);

    /**
     * 내 알림 정보 가져오기
     */
    @GET(GET_USER_NOTIFICATION)
    Call<AlertResponse> getMyAlertStatus(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 내 정보 가져오기
     */
    @GET(GET_USER)
    Call<UserInfoResponse> requestUserInfo(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 내 분석 정보 가져오기
     */
    @GET(GET_USER_ANALYSIS)
    Call<StatisticsResponse> requestStatistics(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 상대 정보 가져오기
     */
    @GET(GET_USER_SEARCH)
    Call<UserSearchResponse> requestSearchUserInfo(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("code") String userCode);

    /**
     * 이름 수정
     */
    @PATCH(PATCH_USER_NAME)
    Call<Void> requestUpdateProfileName(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body NicknameEditRequest request);

    /**
     * 프로필 사진 수정
     */
    @Multipart
    @PATCH(PATCH_USER_PROFILE)
    Call<Void> requestUpdateProfileImage(@Header("Authorization") String authorization, @Part MultipartBody.Part profile);

    /**
     * FCM 알림 토큰 등록 (Todo - 작업 필요)
     */
    @POST(POST_USER_NOTIFICATION_TOKEN)
    Call<Void> InsertToken(@Path("user_id") String userId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body NotificationTokenRequest tokenRequest);

    /**
     * 회원 가입과 로그인
     */
    @POST(POST_USER)
    Call<LoginResponse> requestSignIn(@Body LoginRequest requestBody);

    /**
     * Access Token 갱신
     */
    @POST(POST_USER_AUTH)
    Call<TokenResponse> getRequestToken(@Header("Content-Type") String contentType, @Body TokenRequest refreshToken);

    /**
     * 알림 정보 수정
     */
    @PUT(PUT_USER_NOTIFICATION)
    Call<AlertResponse> requestAlertStatus(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body AlertRequest alertRequest);

    /**
     * FCM 알림 토큰 수정
     */
    @PUT(PUT_USER_NOTIFICATION_TOKEN)
    Call<Void> updateToken(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body NotificationTokenRequest tokenRequest);

}
