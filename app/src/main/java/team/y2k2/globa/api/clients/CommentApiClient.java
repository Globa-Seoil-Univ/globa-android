package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.CommentRequest;
import team.y2k2.globa.api.model.request.FirstCommentRequest;
import team.y2k2.globa.api.model.request.SubCommentRequest;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.api.model.response.SubCommentResponse;
import team.y2k2.globa.api.services.CommentApiService;

public class CommentApiClient extends ApiClient {
    private final CommentApiService apiService;

    public CommentApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if (ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(CommentApiService.class);
    }

    // 댓글 삭제
    public void deleteComment(String folderId, String recordId, String sectionId, String highlightId, String commentId) {
        executeVoidApiCall(apiService.deleteComment(folderId, recordId, sectionId, highlightId, commentId, APPLICATION_JSON, getAuthorization()));
    }

    // 댓글 가져오기
    public CommentResponse getComments(String folderId, String recordId, String sectionId, String highlightId, int page, int count) {
        return executeApiCall(apiService.getComments(folderId, recordId, sectionId, highlightId, APPLICATION_JSON, getAuthorization(), page, count));
    }

    // 대댓글 가져오기
    public SubCommentResponse getSubComments(String folderId, String recordId, String sectionId, String highlightId, String parentId, int page, int count) {
        return executeApiCall(apiService.getSubComments(folderId, recordId, sectionId, highlightId, parentId, APPLICATION_JSON, getAuthorization(), page, count));
    }

    // 댓글 수정
    public Response<?> updateComment(String folderId, String recordId, String sectionId, String highlightId, String commentId, String text) {
        return executeVoidApiCall(apiService.updateComment(folderId, recordId, sectionId, highlightId, commentId, APPLICATION_JSON, getAuthorization(), new CommentRequest(text)));
    }

    // 댓글 최초 추가
    public Response<?> requestInsertFirstComment(String folderId, String recordId, String sectionId, String startIdx, String endIdx, String content) {
        return executeVoidApiCall(apiService.requestInsertFirstComment(folderId, recordId, sectionId, APPLICATION_JSON, getAuthorization(), new FirstCommentRequest(startIdx, endIdx, content)));
    }

    // 댓글 추가 (최초X)
    public Response<?> requestInsertComment(String folderId, String recordId, String sectionId, String highlightId, String content) {
        return executeVoidApiCall(apiService.requestInsertComment(folderId, recordId, sectionId, highlightId, APPLICATION_JSON, getAuthorization(), new CommentRequest(content)));
    }

    // 대댓글 추가
    public void requestInsertSubComment(String folderId, String recordId, String sectionId, String highlightId, String parentId, String content) {
        executeVoidApiCall(apiService.requestInsertSubComment(folderId, recordId, sectionId, highlightId, parentId, APPLICATION_JSON, getAuthorization(), new SubCommentRequest(content)));
    }
}
