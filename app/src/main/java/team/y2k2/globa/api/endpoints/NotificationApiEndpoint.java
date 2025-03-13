package team.y2k2.globa.api.endpoints;

public class NotificationApiEndpoint {
//    알림 삭제
    public static final String DELETE_NOTIFICATION = "/notification/{notification_id}";
//    알림 조회
    public static final String GET_NOTIFICATION = "/notification";
//    안 읽은 알림 개수 조회
    public static final String GET_NOTIFICATION_UNREAD_COUNT = "/notification/unread";
//    안 읽은 알림 여부 조회
    public static final String GET_NOTIFICATION_UNREAD_CHECK = "/notification/unread/check";
//    알림 읽음 처리
    public static final String POST_NOTIFICATION = "/notification/{notification_id}";
}
