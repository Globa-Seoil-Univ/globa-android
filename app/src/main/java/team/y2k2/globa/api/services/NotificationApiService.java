package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.DELETE_NOTIFICATION;
import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.GET_NOTIFICATION;
import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.GET_NOTIFICATION_UNREAD_CHECK;
import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.GET_NOTIFICATION_UNREAD_COUNT;
import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.POST_NOTIFICATION;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCheckResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCountResponse;

public interface NotificationApiService {
    /**
     * 알림 삭제 - (Todo - 작업 필요)
     */
    @DELETE(DELETE_NOTIFICATION)
    Call<NotificationResponse> requestDeleteNotification(@Path("notification_id") String notificationId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 알림 조회
     */
    @GET(GET_NOTIFICATION)
    Call<NotificationResponse> requestGetNotification(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count, @Query("type") String type);

    /**
     * 안 읽은 알림 개수 조회
     */
    @GET(GET_NOTIFICATION_UNREAD_COUNT)
    Call<UnreadNotificationCountResponse> getUnreadNotificationCount(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 안 읽은 알림 여부 조회
     */
    @GET(GET_NOTIFICATION_UNREAD_CHECK)
    Call<UnreadNotificationCheckResponse> getUnreadNotificationCheck(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 알림 읽음 처리
     */
    @POST(POST_NOTIFICATION)
    Call<Void> readNotification(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Path("notification_id") String notificationId);
}
