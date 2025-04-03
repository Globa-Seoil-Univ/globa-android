package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import java.util.List;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.ShareTarget;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;
import team.y2k2.globa.api.model.request.FolderShareAddRequest;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.services.FolderApiService;
import team.y2k2.globa.api.services.FolderShareApiService;

public class FolderApiClient extends ApiClient {
    private final FolderApiService apiService;
    private final FolderShareApiService folderShareApiService;

    public FolderApiClient() {
        super(ApiClient.getInstance(null).getContext());
        if(ApiClient.getInstance(null).getContext() == null) {
            throw new IllegalStateException("context 가 없음. Login 프로세스나 Logout 프로세스에서 다시 확인");
        }
        apiService = ApiClient.getRetrofit().create(FolderApiService.class);
        folderShareApiService = ApiClient.getRetrofit().create(FolderShareApiService.class);

    }

    // 폴더 삭제
    public void requestDeleteFolder(int folderId) {
        executeVoidApiCall(apiService.requestDeleteFolder(folderId, APPLICATION_JSON, getAuthorization()));
    }

    // 폴더 목록 조회
    public FolderResponse requestGetFolders(int page, int count) {
        return executeApiCall(apiService.requestGetFolders(APPLICATION_JSON, getAuthorization(), page, count));
    }

    // 폴더 이름 변경
    public Response<Void> requestUpdateFolderName(int folderId, FolderNameEditRequest request) {
        return executeVoidApiCall(apiService.requestUpdateFolderName(folderId, APPLICATION_JSON, getAuthorization(), request));
    }

    // 유저 폴더 공유
    public Response<Void> requestInsertFolderShareUser(int folderId, int userId, String role) {
        return executeVoidApiCall(folderShareApiService.requestInsertFolderShareUser(folderId, userId, APPLICATION_JSON, getAuthorization(), new FolderShareAddRequest(role)));
    }

    // 폴더 추가
    public Response<Void> requestInsertFolder(String title, List<ShareTarget> shareTargets) {
        return executeVoidApiCall(apiService.requestInsertFolder(APPLICATION_JSON, getAuthorization(), new FolderAddRequest(title, shareTargets)));
    }
}
