package team.y2k2.globa.api.endpoints;

public class CommentApiEndpoint {
//    댓글 삭제
    public static final String DELETE_COMMENT = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment/{commentId}";
//    댓글 목록 조회
    public static final String GET_COMMENT = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment";
//    대댓글 목록 조회
    public static final String GET_SUB_COMMENT = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment/{parentId}";
//    댓글 수정
    public static final String PATCH_COMMENT_SHARE_USER = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment/{commentId}";
//    첫 댓글 추가
    public static final String POST_COMMENT_SHARE = "/folder/{folderId}/record/{recordId}/section/{sectionId}";
//    첫 댓글 추가
    public static final String POST_COMMENT_SHARE_USER = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment";
//    대댓글 추가
    public static final String POST_COMMENT_SUB_COMMENT = "/folder/{folderId}/record/{recordId}/section/{sectionId}/highlight/{highlightId}/comment/{parentId}";
}