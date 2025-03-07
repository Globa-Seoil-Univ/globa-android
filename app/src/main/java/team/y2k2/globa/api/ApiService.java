package team.y2k2.globa.api;

import static team.y2k2.globa.api.endpoints.CommentApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.DictionaryApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.FolderApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.InquiryApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.NoticeApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.NotificationApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.*;
import static team.y2k2.globa.api.endpoints.UserApiEndpoint.*;

import java.util.List;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.request.*;
import team.y2k2.globa.api.model.response.*;
import team.y2k2.globa.main.profile.inquiry.InquiryRequest;

public interface ApiService {
    String API_BASE_URL = "http://192.168.219.111";

    /**
     * Folder Share - 공유 관련 API
     */
    /**
     * 공유 초대 거절
     */
    @DELETE(DELETE_FOLDER_SHARE)
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
    @DELETE(DELETE_FOLDER_SHARE_USER)
    Call<Void> requestDeleteSharePermission(
            @Path("folder_id") int folderId,
            @Path("user_id") int user_id,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 공유된 사용자 조회
     */
    @GET(GET_FOLDER_SHARE_USER)
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
    @PATCH(PATCH_FOLDER_SHARE_USER)
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
    @POST(POST_FOLDER_SHARE)
    Call<Void> requestAcceptShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 사용자 공유 초대
     */
    @POST(POST_FOLDER_SHARE_USER)
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
    @DELETE(DELETE_COMMENT)
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
    @GET(GET_COMMENT)
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
    @GET(GET_SUB_COMMENT)
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
    @PATCH(PATCH_COMMENT_SHARE_USER)
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
    @POST(POST_COMMENT_SHARE)
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
    @POST(POST_COMMENT_SHARE_USER)
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
    @POST(POST_COMMENT_SUB_COMMENT)
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
//    @DELETE(DELETE_ANSWER); 답변 삭제
//    @PATCH(PATCH_ANSWER); 답변 수정
//    @POST(POST_ANSWER); 답변 추가

    /**
     * Record - 음성 관련 API
     */
//    @DELETE(DELETE_RECORD_SHARE_LINK); 문서 링크 공유 취소
    /**
     * 문서 삭제
     */
    @DELETE(DELETE_RECORD)
    Call<Void> requestDeleteRecord(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 문서 추가
     */
    @POST(POST_RECORD)
    Call<Void> requestCreateRecord(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body RecordCreateRequest insertDocumentRequest
    );
    /**
     * 폴더 내 녹음 파일 조회
     */
    @GET(GET_RECORD_IN_FOLDER)
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
    @GET(GET_RECORD_QUIZ)
    Call<List<Quiz>> requestGetQuiz(
            @Path("folder_id") int folderId,
            @Path("record_id") int recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 모든 녹음 파일 조회
     */
    @GET(GET_RECORD_ALL)
    Call<RecordResponse> requestGetRecords(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     * 공유 하는 문서 조회
     */
    @GET(GET_RECORD_SHARING)
    Call<RecordResponse> requestGetRecordsOfSharing(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     *  문서 검색
     */
    @GET(GET_RECORD_SEARCH)
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
    @GET(GET_RECORD_RECEIVING)
    Call<RecordResponse> requestGetRecordsOfReceiving(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );
    /**
     * 녹음 파일 상세 조회
     */
    @GET(GET_RECORD_DETAIL_IN_FOLDER)
    Call<DocsDetailResponse> requestGetDocumentDetail(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 문서 내 시각화 자료 조회
     */
    @GET(GET_RECORD_ANALYSIS_IN_FOLDER)
    Call<StatisticsResponse> requestDocStatistics(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 공부시간 수정
     */
    @PATCH(PATCH_RECORD_STUDY)
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
    @PATCH(PATCH_RECORD_NAME)
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
    @PATCH(PATCH_RECORD_MOVE_FOLDER)
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
    @POST(POST_RECORD_QUIZ)
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
    @DELETE(DELETE_NOTIFICATION)
    Call<NotificationResponse> requestDeleteNotification(
            @Path("notification_id") String notificationId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 알림 조회
     */
    @GET(GET_NOTIFICATION)
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
    @GET(GET_NOTIFICATION_UNREAD_COUNT)
    Call<UnreadNotificationCountResponse> getUnreadNotificationCount(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 안 읽은 알림 여부 조회
     */
    @GET(GET_NOTIFICATION_UNREAD_CHECK)
    Call<UnreadNotificationCheckResponse> getUnreadNotificationCheck(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 알림 읽음 처리
     */
    @POST(POST_NOTIFICATION)
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
    @DELETE(DELETE_FOLDER)
    Call<Void> requestDeleteFolder(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 폴더 목록 조회
     */
    @GET(GET_FOLDER)
    Call<FolderResponse> requestGetFolders(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );
    /**
     * 폴더 이름 수정
     */
    @PATCH(PATCH_FOLDER_NAME)
    Call<Void> requestUpdateFolderName(
            @Path("folder_id") int folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body FolderNameEditRequest folderNameEditRequest
    );
    /**
     * 폴더 추가
     */
    @POST(POST_FOLDER)
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
    @GET(GET_DICTIONARY)
    Call<KeywordDetailResponse> searchDictionary(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("keyword") String keyword
    );
    /**
     * 단어 추가 (Todo - 작업 필요)
     */
    @POST(POST_DICTIONARY)
    Call<KeywordDetailResponse> insertDictionary(
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
    @GET(GET_INQUIRY)
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
    @GET(GET_INQUIRY_DETAIL)
    Call<InquiryDetailResponse> requestGetInquiryDetail(
            @Path("inquiry_id") String inquiryId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 문의 등록
     */
    @POST(POST_INQUIRY)
    Call<Void> requestInsertInquiry(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body InquiryRequest inquiryRequest
    );

    /**
     * Notice - 공지 관련 API
     */
//    @GET(GET_NOTICE_DETAIL); 공지사항 상세 조회
    /**
     * 간단 공지사항 조회
     */
    @GET(GET_NOTICE)
    Call<List<NoticeResponse>> requestPromotion(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int count
    );
//    @POST(POST_NOTICE); 공지사항 추가

    /**
     * User - 사용자 관련 API
     */
    /**
     * 회원 탈퇴
     * @surveyType 1: 서비스 사용 불편, 2: 정확성 낮음, 3: 기능 부족, 4: 다른 서비스 선호
     * @content 추가 내용
     */
    @DELETE(DELETE_USER)
    Call<Void> requestWithdrawUser(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body WithdrawRequest withdrawRequest
    );
    /**
     * 내 알림 정보 가져오기
     */
    @GET(GET_USER_NOTIFICATION)
    Call<AlertResponse> getMyAlertStatus(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 내 정보 가져오기
     */
    @GET(GET_USER)
    Call<UserInfoResponse> requestUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 내 분석 정보 가져오기
     */
    @GET(GET_USER_ANALYSIS)
    Call<StatisticsResponse> requestStatistics(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );
    /**
     * 상대 정보 가져오기
     */
    @GET(GET_USER_SEARCH)
    Call<UserSearchResponse> requestSearchUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("code") String userCode
    );
    /**
     * 이름 수정
     */
    @PATCH(PATCH_USER_NAME)
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
    @PATCH(PATCH_USER_PROFILE)
    Call<Void> requestUpdateProfileImage(
            @Path("user_id") String userId,
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part profile
    );
    /**
     * FCM 알림 토큰 등록 (Todo - 작업 필요)
     */
    @POST(POST_USER_NOTIFICATION_TOKEN)
    Call<Void> InsertToken(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationTokenRequest tokenRequest
    );
    /**
     * 회원 가입과 로그인 (Todo - 작업 필요)
     */
    @POST(POST_USER)
    Call<LoginResponse> requestSignIn(@Body LoginRequest requestBody);
    /**
     * Access Token 갱신
     */
    @POST(POST_USER_AUTH)
    Call<TokenResponse> getRequestToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequest refreshToken
    );
    /**
     * 알림 정보 수정
     */
    @PUT(PUT_USER_NOTIFICATION)
    Call<AlertResponse> requestAlertStatus(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body AlertRequest alertRequest
    );
    /**
     * FCM 알림 토큰 수정
     */
    @PUT(PUT_USER_NOTIFICATION_TOKEN)
    Call<Void> updateToken(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationTokenRequest tokenRequest
    );
}