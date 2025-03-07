package team.y2k2.globa.api.endpoints;

public class AnswerApiEndpoint {
//    답변 삭제
    public static final String DELETE_ANSWER = "/inquiry/{inquiryId}/answer/{answerId}";
//    답변 수정
    public static final String PATCH_ANSWER = "inquiry/{inquiryId}/answer/{answerId}";
//    답변 추가
    public static final String POST_ANSWER = "inquiry/{inquiryId}/answer";
}
