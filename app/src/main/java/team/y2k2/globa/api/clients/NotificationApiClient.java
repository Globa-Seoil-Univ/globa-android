package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCheckResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCountResponse;
import team.y2k2.globa.api.services.NotificationApiService;

public class NotificationApiClient extends ApiClient {
    private NotificationApiService apiService;

    public NotificationApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(NotificationApiService.class);
    }

    // 알림 가져오기
    public NotificationResponse requestGetNotification(String type, int page, int count) {
        return executeApiCall(apiService.requestGetNotification(APPLICATION_JSON, getAuthorization(), page, count, type));
    }

    // 알림 가져오기
    public NotificationResponse requestNotification(String type) {
        return executeApiCall(apiService.requestGetNotification(APPLICATION_JSON, getAuthorization(), 1, 100, type));
    }

    // 안 읽은 알림 개수 조회
    public UnreadNotificationCountResponse getUnreadNotificationCount() {
        return executeApiCall(apiService.getUnreadNotificationCount(APPLICATION_JSON, getAuthorization()));
    }

    // 안읽은 알림 호출
    public UnreadNotificationCheckResponse getUnreadNotificationCheck() {
        return executeApiCall(apiService.getUnreadNotificationCheck(APPLICATION_JSON, getAuthorization()));
    }

    // 알림 읽음 처리
    public Response<Void> updateReadNotification(String notificationId) {
        return executeVoidApiCall(apiService.readNotification(APPLICATION_JSON, getAuthorization(), notificationId));
    }
}
