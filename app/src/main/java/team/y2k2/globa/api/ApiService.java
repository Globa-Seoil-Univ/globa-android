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
import retrofit2.http.PATCH;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.NicknameEditRequest;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.docs.edit.DocsNameEditRequest;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.DocsUploadRequest;
import team.y2k2.globa.api.model.response.DocsUploadResponse;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.main.profile.inquiry.InquiryRequest;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.response.NotificationInquiryResponse;

public interface ApiService {

//    String API_BASE_URL = "http://1.209.165.82:8080";
//    String API_BASE_URL = "https://1.209.165.82:8080";
//    String API_BASE_URL = "http://192.168.219.111:8080";
    String API_BASE_URL = "https://globa.tetraplace.com";
    /**
     * 토큰 갱신
     */

    @POST("/auth")
    Call<TokenResponse> getRefreshToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequest refreshToken
    );

    /**
     * 회원가입 및 로그인
     */
    @POST("/user")
    Call<LoginResponse> requestSignIn(@Body LoginRequest requestBody);

    /**
     * 내 정보 가져오기
     */
    @GET("/user")
    Call<UserInfoResponse> requestUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );

    /**
     * 사용자 검색
     */
    @GET("/user/search")
    Call<UserInfoResponse> requestSearchUserInfo(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("code") String userCode
    );

    /**
     * 프로필 사진 수정
     */
    @Multipart
    @POST("/user/{user_id}/profile")
    Call<Void> requestUpdateProfileImage(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType, // "multipart/form-data"
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part profile
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
     * 사용자 탈퇴
     * @surveyType 1: 서비스 사용 불편, 2: 정확성 낮음, 3: 기능 부족, 4: 다른 서비스 선호
     * @content 추가 내용
     */
    @POST("/user")
    void requestWithdrawUser(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body int surveyType,
            @Body String content
    );

    /**
     *
     */
    @POST("/folder/{folder_id}/record")
    Call<DocsUploadResponse> createRecord(
            @Path("folder_id") String folderId,
            @Body DocsUploadRequest requestBody
    );


    /**
     * 문서 추가
     */
    @POST("/folder/{folder_id}/record")
    Call<Void> requestCreateRecord(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body RecordCreateRequest insertDocumentRequest
    );

    /**
     * 공지사항 미리 가져오기
     */
    @GET("/notice/intro")
    Call<List<NoticeResponse>> requestPromotion(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int count
    );

    /**
     * 폴더 가져오기
     */
    @GET("/folder")
    Call<List<FolderResponse>> requestGetFolders(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );


    /**
     * 문서 가져오기
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
     * 폴더 추가
     */
    @POST("/folder")
    Call<Void> requestInsertFolder(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Nullable @Body FolderAddRequest folderAddRequest
    );

    /**
     * 문서 이름 수정
     * - 문서 이름을 변경합니다.
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
     * - 문서를 이동합니다.
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
     * 폴더 삭제
     */
    @GET("/folder/{folder_id}")
    void requestDeleteFolder(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );

    /**
     * 공유된 사용자 가져오기
     */
    @GET("/folder/{folder_id}/share/user")
    Objects requestFoloderShareUser(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
//            @Body 여기에 유저 목록을 나타냄
    );

    /**
     * 공유 추가
     */
    @POST("/folder/{folder_id}/share/user/{user_id}")
    void requestInsertFolderShareUser(
            @Path("folder_id") String folderId,
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body role : R , W
    );

    /**
     * 공유 권한 변경
     */
    @POST("/folder/{folder_id}/share/{share_id}/user/{user_id}")
    void requestUpdateSharePermission(
            @Path("folder_id") String folderId,
            @Path("share_id") String share_id,
            @Path("user_id") String user_id,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body
    );

    /**
     * 공유 삭제
     */
    @POST("/folder/{folder_id}/share/{share_id}/user/{user_id}")
    void requestDeleteSharePermission(
            @Path("folder_id") String folderId,
            @Path("share_id") String share_id,
            @Path("user_id") String user_id,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
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
     * 공유 초대 거절
     */
    @DELETE("/folder/{folder_id}/share/{share_id}")
    Call<Void> requestDeniedShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareid,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationRequest notificationRequest
    );

    /**
     * 문서 가져오기
      */
    @GET("/folder/{folder_id}/record")
    Objects requestGetDocument(
            @Path("folder_id") String folderId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int number,
            @Query("count") int count
//            @Body
    );




    /**
     * 모든 문서 가져오기
     */
    @GET("/record")
    Call<RecordResponse> requestGetRecords(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number
    );

    /**
     * 문서 상세 가져오기
     */
    @GET("/folder/{folder_id}/record/{record_id}")
    Call<DocsDetailResponse> requestGetDocumentDetail(
            @Path("folder_id") String folderId,
            @Path("record_id") String recordId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );

    /**
     * 문의하기
     *
     * @return
     */
    @POST("/inquiry")
    Call<Void> requestInsertInquiry(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body InquiryRequest inquiryRequest
    );
    /**
     * 문의내역 목록 조회
     *
     * @return
     */
    @GET("/inquiry")
    Call<NotificationInquiryResponse> requestGetInquires(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort
    );

    @GET("/inquiry/{inquiry_id}")
    Call<InquiryDetailResponse> requestGetInquiryDetail(
            @Path("inquiry_id") String inquiryId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );


    @GET("/notification")
    Call<NotificationResponse> requestGetAllNotification(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );


}
