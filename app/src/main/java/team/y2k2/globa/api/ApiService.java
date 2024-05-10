package team.y2k2.globa.api;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.docs.upload.DocsUploadRequestModel;
import team.y2k2.globa.docs.upload.DocsUploadResponseModel;
import team.y2k2.globa.login.LoginResponse;
import team.y2k2.globa.main.NoticeResponse;
import team.y2k2.globa.main.folder.FolderResponse;
import team.y2k2.globa.main.profile.UserInfoResponse;
import team.y2k2.globa.network.jwt.TokenRequestModel;
import team.y2k2.globa.network.jwt.TokenResponseModel;
import team.y2k2.globa.login.LoginRequest;

public interface ApiService {

//    String API_BASE_URL = "http://1.209.165.82:8080";
    String API_BASE_URL = "http://192.168.219.111:8080";
//    String API_BASE_URL = "https://globa.tetraplace.com";
    /**
     * 토큰 갱신
     */

    @POST("/auth")
    Call<TokenResponseModel> getRefreshToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequestModel refreshToken
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
    @POST("/user/{user_id}/profile")
    void requestUpdateProfileImage(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType, // "multipart/form-data"
            @Header("Authorization") String authorization
//            @Body 이미지 파일 profile
    );

    /**
     * 이름 수정
     */
    @POST("/user/{user_id}/name")
    void requestUpdateProfileName(
            @Path("user_id") String userId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body String name
    );

    /**
     * 사용자 탈퇴
     * @surveyType 1: 서비스 사용 불편, 2: 정확성 낮음, 3: 기능 부족, 4: 다른 서비스 선호
     * @content 추가 내용
     */
    @POST("/user")
    void requestWithdrawUser(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body
    );

    /**
     *
     */
    @POST("/folder/{folder_id}/record")
    Call<DocsUploadResponseModel> createRecord(
            @Path("folder_id") String folderId,
            @Body DocsUploadRequestModel requestBody
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
    Call<FolderResponse> requestGetFolders(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("count") int count
    );

    /**
     * 폴더 추가
     */
    @POST("/folder")
    void requestCreateFolder(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//          @Body
    );

    /**
     * 폴더 이름 수정
     * - 폴더 이름을 변경합니다.
     */
    @POST("/folder/{folder_id}/name")
    void requestUpdateFolderName(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body String title
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
    void requestAcceptShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareId,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
    );

    /**
     * 공유 초대 거절
     */
    @POST("/folder/{folder_id}/share/{share_id}")
    void requestDeniedShareInvite(
            @Path("folder_id") String folderId,
            @Path("share_id") String shareid,
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body notificationId
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
    Objects requestGetAllDocument(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Query("count") int number,
            @Query("sort") String string
//            @Body
    );

    /**
     * 문서 상세 가져오기
     */
    @GET("/folder/{folder_id}/record/{record_id}")
    Objects requestGetDocumentDetail(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization
//            @Body
    );

}
