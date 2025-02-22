package team.y2k2.globa.api;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.request.AlertRequest;
import team.y2k2.globa.api.model.request.CommentRequest;
import team.y2k2.globa.api.model.request.FirstCommentRequest;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;
import team.y2k2.globa.api.model.request.FolderPermissionChangeRequest;
import team.y2k2.globa.api.model.request.FolderShareAddRequest;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.request.NicknameEditRequest;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.request.NotificationTokenRequest;
import team.y2k2.globa.api.model.request.QuizResultRequest;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.request.StudyTimeRequest;
import team.y2k2.globa.api.model.request.SubCommentRequest;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.request.WithdrawRequest;
import team.y2k2.globa.api.model.response.AlertResponse;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.NotificationInquiryResponse;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.model.response.SubCommentResponse;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCheckResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCountResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.api.model.response.UserSearchResponse;
import team.y2k2.globa.api.model.request.DocsNameEditRequest;
import team.y2k2.globa.main.profile.inquiry.InquiryRequest;

public interface ApiService {
    String API_BASE_URL = "http://192.168.219.111";

    /**
     * Folder Share - 공유 관련 API
     */
    /**
     * 공유 초대 거절
     */
    @DELETE("/folder/{folder_id}/share/{share_id}")
    Call<Void> requestDeniedShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationRequest notificationRequest
    );
    /**
     * 사용자 공유 초대 취소
     */
    @DELETE("/folder/{folder_id}/share/user/{user_id}")
    Call<Void> requestDeleteSharePermission(
            @Path("folder_id") int folderId,
            @Path("user_id") int user_id,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 공유된 사용자 조회
     */
    @GET("/folder/{folder_id}/share/user")
    Call<FolderPermissionResponse> requestFoloderShareUser(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 사용자 공유 초대 변경
     */
    @PATCH("/folder/{folder_id}/share/user/{user_id}")
    Call<Void> requestUpdateSharePermission(
            @Path("folder_id") int folder_id,
            @Path("user_id") int user_id,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body FolderPermissionChangeRequest folderPermissionChangeRequest
    );
    /**
     * 공유 초대 수락
     */
    @POST("/folder/{folder_id}/share/{share_id}")
    Call<Void> requestAcceptShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 사용자 공유 초대
     */
    @POST("/folder/{folder_id}/share/user/{user_id}")
    Call<Void> requestInsertFolderShareUser(
            @Path("folder_id") int folderId,
            @Path("user_id") int userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body FolderShareAddRequest folderShareAddRequest
    );

    /**
     * Comment - 댓글 관련 API
     */
    /**
     * 댓글 삭제
     */
    @DELETE("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment/{comment_id}")
    Call<Void> deleteComment(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Path("comment_id") String commentId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 댓글 목록 조회
     */
    @GET("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment")
    Call<CommentResponse> getComments(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 대댓글 목록 조회
     */
    @GET("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment/{parent_id}")
    Call<SubCommentResponse> getSubComments(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Path("parent_id") String parentId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 댓글 수정
     */
    @PATCH("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment/{comment_id}")
    Call<Void> updateComment(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Path("comment_id") String commentId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body CommentRequest request
    );
    /**
     * 첫 댓글 추가
     */
    @POST("/folder/{folder_id}/record/{record_id}/section/{section_id}")
    Call<Void> requestInsertFirstComment(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body FirstCommentRequest request
    );
    /**
     * 댓글 추가
     */
    @POST("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment")
    Call<Void> requestInsertComment(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body CommentRequest request
    );
    /**
     * 대댓글 추가
     */
    @POST("/folder/{folder_id}/record/{record_id}/section/{section_id}/highlight/{highlight_id}/comment/{parent_id}")
    Call<Void> requestInsertSubComment(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Path("section_id") String sectionId,
            @Path("highlight_id") String highlightId,
            @Path("parent_id") String parentId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body SubCommentRequest request
    );


    /**
     * Answer - 답변 관련 API
     */
//    @DELETE("/inquiry/{inquiry_id}/answer/{answer_id}"); 답변 삭제
//    @PATCH("/inquiry/{inquiry_id}/answer/{answer_id}"); 답변 수정
//    @POST("/inquiry/{inquiry_id}/answer"); 답변 추가

    /**
     * Record - 음성 관련 API
     */
//    @DELETE("folder/{folder_id}/record/{record_id}/link"); 문서 링크 공유 취소
    /**
     * 문서 삭제
     */
    @DELETE("/folder/{folder_id}/record/{record_id}")
    Call<Void> requestDeleteRecord(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 폴더 내 녹음 파일 조회
     */
    @POST("/folder/{folder_id}/record")
    Call<Void> requestCreateRecord(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body RecordCreateRequest insertDocumentRequest
    );
    /**
     * 폴더 내 녹음 파일 조회
     */
    @GET("/folder/{folder_id}/record")
    Call<FolderInsideRecordResponse> requestGetFolderInside(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 퀴즈 조회
     */
    @GET("/folder/{folder_id}/record/{record_id}/quiz")
    Call<List<Quiz>> requestGetQuiz(
            @Path("folder_id") int folderId,
            @Path("record_id") int recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 모든 녹음 파일 조회
     */
    @GET("/record")
    Call<RecordResponse> requestGetRecords(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     * 공유 하는 문서 조회
     */
    @GET("/record/sharing")
    Call<RecordResponse> requestGetRecordsOfSharing(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     *  문서 검색
     */
    @GET("/record/search")
    Call<SearchResponse> searchRecordForKeyword(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 공유 받는 문서 조회
     */
    @GET("/record/receiving")
    Call<RecordResponse> requestGetRecordsOfReceiving(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     * 녹음 파일 상세 조회
     */
    @GET("/folder/{folder_id}/record/{record_id}")
    Call<DocsDetailResponse> requestGetDocumentDetail(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 문서 내 시각화 자료 조회
     */
    @GET("/folder/{folder_id}/record/{record_id}/analysis")
    Call<StatisticsResponse> requestDocStatistics(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 공부시간 수정
     */
    @PATCH("/folder/{folder_id}/record/{record_id}/study")
    Call<Void> requestStudyTime(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body StudyTimeRequest studyTimeRequest
    );
    /**
     * 문서 이름 수정
     */
    @PATCH("/folder/{folder_id}/record/{record_id}/name")
    Call<Void> requestUpdateRecordName(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body DocsNameEditRequest docsNameEditRequest
    );
    /**
     * 문서 폴더 이동
     */
    @PATCH("/folder/{folder_id}/record/{record_id}/folder")
    Call<Void> requestUpdateDocsMove(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body DocsMoveRequest docsMoveRequest
    );
    /**
     * 퀴즈 결과 추가
     */
    @POST("/folder/{folder_id}/record/{record_id}/quiz")
    Call<Void> requestInsertQuizResult(
            @Path("folder_id") int folderId,
            @Path("record_id") int recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body QuizResultRequest result
    );

    /**
     * FCM - Firebase Cloud Messaging을 사용하여 알림을 보내는 API
     */
//    @POST("/fcm/send"); 특정 토픽 알림 전송

    /**
     * Notification - 알림 관련 API
     */
    /**
     * 알림 삭제 - (Todo - 작업 필요)
     */
    @DELETE("/notification/{notification_id}")
    Call<NotificationResponse> requestDeleteNotification(
            @Path("notification_id") String notificationId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 알림 조회
     */
    @GET("/notification")
    Call<NotificationResponse> requestGetNotification(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count,
            @Query("type") String type
    );
    /**
     * 안 읽은 알림 개수 조회
     */
    @GET("/notification/unread/count")
    Call<UnreadNotificationCountResponse> getUnreadNotificationCount(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 안 읽은 알림 여부 조회
     */
    @GET("/notification/unread/check")
    Call<UnreadNotificationCheckResponse> getUnreadNotificationCheck(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 알림 읽음 처리
     */
    @POST("/notification/{notification_id}")
    Call<Void> readNotification(
            @Path("notification_id") String notificationId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );

    /**
     * Folder - 폴더 관련 API
     */
    /**
     * 폴더 삭제
     */
    @DELETE("/folder/{folder_id}")
    Call<Void> requestDeleteFolder(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 폴더 목록 조회
     */
    @GET("/folder")
    Call<FolderResponse> requestGetFolders(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 폴더 이름 수정
     */
    @PATCH("/folder/{folder_id}/name")
    Call<Void> requestUpdateFolderName(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body FolderNameEditRequest folderNameEditRequest
    );
    /**
     * 폴더 추가
     */
    @POST("/folder")
    Call<Void> requestInsertFolder(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Nullable @Body FolderAddRequest folderAddRequest
    );

    /**
     * Dictionary - 단어 관련 API
     */
    /**
     * 단어 조회
     */
    @GET("/dictionary")
    Call<KeywordDetailResponse> searchKeyword(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("keyword") String keyword
    );
    /**
     * 단어 추가 (Todo - 작업 필요)
     */
    @POST("/dictionary")
    Call<KeywordDetailResponse> insertKeyword(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("keyword") String keyword
    );

    /**
     * Inquiry 문의 관련 API
     */
    /**
     * 문의 조회
     */
    @GET("/inquiry")
    Call<NotificationInquiryResponse> requestGetInquires(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort
    );
    /**
     * 문의 상세 조회
     */
    @GET("/inquiry/{inquiry_id}")
    Call<InquiryDetailResponse> requestGetInquiryDetail(
            @Path("inquiry_id") String inquiryId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 문의 등록
     */
    @POST("/inquiry")
    Call<Void> requestInsertInquiry(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body InquiryRequest inquiryRequest
    );

    /**
     * Notice - 공지 관련 API
     */
//    @GET("/notice/{notice_id}"); 공지사항 상세 조회
    /**
     * 간단 공지사항 조회
     */
    @GET("/notice/intro")
    Call<List<NoticeResponse>> requestPromotion(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int count
    );
//    @POST("/notice"); 공지사항 추가

    /**
     * User - 사용자 관련 API
     */
    /**
     * 회원 탈퇴
     * @surveyType 1: 서비스 사용 불편, 2: 정확성 낮음, 3: 기능 부족, 4: 다른 서비스 선호
     * @content 추가 내용
     */
    @DELETE("/user")
    Call<Void> requestWithdrawUser(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body WithdrawRequest withdrawRequest
    );
    /**
     * 내 알림 정보 가져오기
     */
    @GET("/user/{user_id}/notification")
    Call<AlertResponse> getMyAlertStatus(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 내 정보 가져오기
     */
    @GET("/user")
    Call<UserInfoResponse> requestUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 내 분석 정보 가져오기
     */
    @GET("/user/{user_id}/analysis")
    Call<StatisticsResponse> requestStatistics(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 상대 정보 가져오기
     */
    @GET("/user/search")
    Call<UserSearchResponse> requestSearchUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("code") String userCode
    );
    /**
     * 이름 수정
     */
    @PATCH("/user/{user_id}/name")
    Call<Void> requestUpdateProfileName(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NicknameEditRequest request
    );
    /**
     * 프로필 사진 수정
     */
    @Multipart
    @PATCH("/user/{user_id}/profile")
    Call<Void> requestUpdateProfileImage(
            @Path("user_id") String userId,
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part profile
    );
    /**
     * FCM 알림 토큰 등록 (Todo - 작업 필요)
     */
    @POST("/user/{user_id}/notification/token")
    Call<Void> InsertToken(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationTokenRequest tokenRequest
    );
    /**
     * 회원 가입과 로그인 (Todo - 작업 필요)
     */
    @POST("/user")
    Call<LoginResponse> requestSignIn(@Body LoginRequest requestBody);
    /**
     * Access Token 갱신
     */
    @POST("/user/auth")
    Call<TokenResponse> getRequestToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequest refreshToken
    );
    /**
     * 알림 정보 수정
     */
    @PUT("/user/{user_id}/notification")
    Call<AlertResponse> requestAlertStatus(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body AlertRequest alertRequest
    );
    /**
     * FCM 알림 토큰 수정
     */
    @PUT("/user/{user_id}/notification/token")
    Call<Void> updateToken(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationTokenRequest tokenRequest
    );
}