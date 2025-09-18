package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import android.content.Context;

import okhttp3.MultipartBody;
import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
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
import team.y2k2.globa.api.services.UserApiService;

public class UserApiClient extends ApiClient {
    private final UserApiService apiService;

    public UserApiClient(Context context) {
        super(ApiClient.getInstance(context).getContext());
        apiService = ApiClient.getRetrofit().create(UserApiService.class);
    }

    // 회원 탈퇴
    public Response<Void> requestWithdrawUser(int surveyType, String content) {
        return executeVoidApiCall(apiService.requestWithdrawUser(APPLICATION_JSON, getAuthorization(), new WithdrawRequest(surveyType, content)));
    }

    // 알림 상태 조회
    public AlertResponse getMyAlertStatus() {
        return executeApiCall(apiService.getMyAlertStatus(APPLICATION_JSON, getAuthorization()));
    }

    public UserInfoResponse requestUserInfo() {
        return executeApiCall(apiService.requestUserInfo(APPLICATION_JSON, getAuthorization()));
    }

    // 유저 전체 통계
    public StatisticsResponse requestStatistics(String userId) {
        return executeApiCall(apiService.requestStatistics(userId, APPLICATION_JSON, getAuthorization()));
    }

    // 유저 찾기
    public UserSearchResponse requestSearchUserInfo(String userCode) {
        return executeApiCall(apiService.requestSearchUserInfo(APPLICATION_JSON, getAuthorization(), userCode));
    }

    // 사용자 이름 변경
    public Response<Void> requestUpdateProfileName(String newNickname) {
        return executeVoidApiCall(apiService.requestUpdateProfileName(APPLICATION_JSON, getAuthorization(), new NicknameEditRequest(newNickname)));
    }

    // 프로필 사진 변경
    public Response<Void> requestUpdateProfileImage(MultipartBody.Part multipartBody) {
        return executeVoidApiCall(apiService.requestUpdateProfileImage(getAuthorization(), multipartBody));
    }

    public LoginResponse requestSignIn(LoginRequest request) {
        return executeApiCall(apiService.requestSignIn(request));
    }

    public TokenResponse requestToken(TokenRequest request) {
        return executeApiCall(apiService.getRequestToken(APPLICATION_JSON, request));
    }

    // 알림 상태 수정
    public AlertResponse requestAlertStatus(AlertRequest alertRequest) {
        return executeApiCall(apiService.requestAlertStatus(APPLICATION_JSON, getAuthorization(), alertRequest));
    }

    // FCM 토큰 업데이트
    public Response<Void> updateToken(String token) {
        return executeVoidApiCall(apiService.updateToken(APPLICATION_JSON, getAuthorization(), new NotificationTokenRequest(token)));
    }
}
