package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.request.FolderPermissionChangeRequest;
import team.y2k2.globa.api.model.request.NotificationRequest;
import team.y2k2.globa.api.model.response.FolderPermissionResponse;
import team.y2k2.globa.api.services.FolderShareApiService;

public class FolderShareApiClient extends ApiClient {
    private final FolderShareApiService apiService;

    public FolderShareApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(FolderShareApiService.class);
    }

    // 초대 거절
    public void requestDeniedShareInvite(String folderId, String shareId, String notificationId) {
        executeVoidApiCall(apiService.requestDeniedShareInvite(folderId, shareId, APPLICATION_JSON, getAuthorization(), new NotificationRequest(notificationId)));
    }

    // 유저 권한 삭제
    public Response<Void> requestDeleteSharePermission(int folderId, int userId) {
        return executeVoidApiCall(apiService.requestDeleteSharePermission(folderId, userId, APPLICATION_JSON, getAuthorization()));
    }

    // 유저 폴더 공유
    public FolderPermissionResponse requestFolderShareUser(int folderId, int page, int count) {
        return executeApiCall(apiService.requestFolderShareUser(folderId, APPLICATION_JSON, getAuthorization(), page, count));
    }

    // 유저 권한 변경
    public Response<Void> requestUpdateSharePermission(int folderId, int userId, String userRole) {
        return executeVoidApiCall(apiService.requestUpdateSharePermission(folderId, userId, APPLICATION_JSON, getAuthorization(), new FolderPermissionChangeRequest(userRole)));
    }

    // 공유 초대 수락
    public void requestAcceptShareInvite(String folderId, String shareId) {
        executeVoidApiCall(apiService.requestAcceptShareInvite(folderId, shareId, APPLICATION_JSON, getAuthorization()));
    }
}
