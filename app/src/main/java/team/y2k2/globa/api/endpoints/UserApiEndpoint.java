package team.y2k2.globa.api.endpoints;

public class UserApiEndpoint {
//    회원 탈퇴
    public static final String DELETE_USER = "/user";
//    내 알림 정보 가져오기
    public static final String GET_USER_NOTIFICATION = "/user/notification";
//    내 정보 가져오기
    public static final String GET_USER = "/user";
//    내 분석 정보 가져오기
    public static final String GET_USER_ANALYSIS = "/user/{user_id}/analysis";
//    상대 정보 가져오기
    public static final String GET_USER_SEARCH = "/user/search";
//    이름 수정
    public static final String PATCH_USER_NAME = "/user/name";
//    프로필 사진 수정
    public static final String PATCH_USER_PROFILE = "/user/{user_id}/profile";
//    FCM 알림 토큰 등록
    public static final String POST_USER_NOTIFICATION_TOKEN = "/user/{user_id}/notification/token";
//    회원 가입과 로그인
    public static final String POST_USER = "/user";
//    Access Token 갱신
    public static final String POST_USER_AUTH = "/user/auth";
//    알림 정보 수정
    public static final String PUT_USER_NOTIFICATION = "/user/notification";
//    FCM 알림 토큰 수정
    public static final String PUT_USER_NOTIFICATION_TOKEN = "/user/{user_id}/notification/token";
}