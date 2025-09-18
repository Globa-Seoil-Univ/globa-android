package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.DELETE_COMMENT;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.GET_COMMENT;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.GET_SUB_COMMENT;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.PATCH_COMMENT_SHARE_USER;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.POST_COMMENT_SHARE;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.POST_COMMENT_SHARE_USER;
import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.POST_COMMENT_SUB_COMMENT;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.CommentRequest;
import team.y2k2.globa.api.model.request.FirstCommentRequest;
import team.y2k2.globa.api.model.request.SubCommentRequest;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.api.model.response.SubCommentResponse;

public interface CommentApiService {
    /**
     * 댓글 삭제
     */
    @DELETE(DELETE_COMMENT)
    Call<Void> deleteComment(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Path("commentId") String commentId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 댓글 목록 조회
     */
    @GET(GET_COMMENT)
    Call<CommentResponse> getComments(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count);

    /**
     * 대댓글 목록 조회
     */
    @GET(GET_SUB_COMMENT)
    Call<SubCommentResponse> getSubComments(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Path("parentId") String parentId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count);

    /**
     * 댓글 수정
     */
    @PATCH(PATCH_COMMENT_SHARE_USER)
    Call<Void> updateComment(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Path("commentId") String commentId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body CommentRequest request);

    /**
     * 첫 댓글 추가
     */
    @POST(POST_COMMENT_SHARE)
    Call<Void> requestInsertFirstComment(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body FirstCommentRequest request);

    /**
     * 댓글 추가
     */
    @POST(POST_COMMENT_SHARE_USER)
    Call<Void> requestInsertComment(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body CommentRequest request);

    /**
     * 대댓글 추가
     */
    @POST(POST_COMMENT_SUB_COMMENT)
    Call<Void> requestInsertSubComment(@Path("folderId") String folderId, @Path("recordId") String recordId, @Path("sectionId") String sectionId, @Path("highlightId") String highlightId, @Path("parentId") String parentId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body SubCommentRequest request);

}
