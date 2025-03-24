package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.DELETE_FOLDER_SHARE;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.DELETE_FOLDER_SHARE_USER;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.GET_FOLDER_SHARE_USER;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.PATCH_FOLDER_SHARE_USER;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.POST_FOLDER_SHARE;
import static team.y2k2.globa.api.endpoints.FolderShareApiEndpoint.POST_FOLDER_SHARE_USER;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.FolderPermissionChangeRequest;
import team.y2k2.globa.api.model.request.FolderShareAddRequest;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;

public interface FolderShareApiService {
    /**
     * 공유 초대 거절
     */
    @DELETE(DELETE_FOLDER_SHARE)
    Call<Void> requestDeniedShareInvite(@Path("folder_id") String folderId, @Path("share_id") String shareId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body NotificationRequest notificationRequest);

    /**
     * 사용자 공유 초대 취소
     */
    @DELETE(DELETE_FOLDER_SHARE_USER)
    Call<Void> requestDeleteSharePermission(@Path("folder_id") int folderId, @Path("user_id") int user_id, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 공유된 사용자 조회
     */
    @GET(GET_FOLDER_SHARE_USER)
    Call<FolderPermissionResponse> requestFolderShareUser(@Path("folder_id") int folderId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count);

    /**
     * 사용자 공유 초대 변경
     */
    @PATCH(PATCH_FOLDER_SHARE_USER)
    Call<Void> requestUpdateSharePermission(@Path("folder_id") int folder_id, @Path("user_id") int user_id, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body FolderPermissionChangeRequest folderPermissionChangeRequest);

    /**
     * 공유 초대 수락
     */
    @POST(POST_FOLDER_SHARE)
    Call<Void> requestAcceptShareInvite(@Path("folder_id") String folderId, @Path("share_id") String shareId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 사용자 공유 초대
     */
    @POST(POST_FOLDER_SHARE_USER)
    Call<Void> requestInsertFolderShareUser(@Path("folder_id") int folderId, @Path("user_id") int userId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body FolderShareAddRequest folderShareAddRequest);

}
