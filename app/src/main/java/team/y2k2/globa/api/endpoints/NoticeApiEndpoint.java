package team.y2k2.globa.api.endpoints;

public class NoticeApiEndpoint {
    // 공지사항 조회
    public static final String GET_NOTICE_DETAIL = "/notice/{noticeId}";
    // 간단 공지사항 조회
    public static final String GET_NOTICE = "/notice/intro";
    // 공지사항 추가
    public static final String POST_NOTICE = "/notice";
}
