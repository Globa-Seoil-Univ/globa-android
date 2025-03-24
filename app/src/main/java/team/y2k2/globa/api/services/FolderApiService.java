package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.FolderApiEndpoint.DELETE_FOLDER;
import static team.y2k2.globa.api.endpoints.FolderApiEndpoint.GET_FOLDER;
import static team.y2k2.globa.api.endpoints.FolderApiEndpoint.PATCH_FOLDER_NAME;
import static team.y2k2.globa.api.endpoints.FolderApiEndpoint.POST_FOLDER;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.request.FolderNameEditRequest;
import team.y2k2.globa.api.model.response.FolderResponse;

public interface FolderApiService {
    /**
     * 폴더 삭제
     */
    @DELETE(DELETE_FOLDER)
    Call<Void> requestDeleteFolder(@Path("folder_id") int folderId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 폴더 목록 조회
     */
    @GET(GET_FOLDER)
    Call<FolderResponse> requestGetFolders(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count);

    /**
     * 폴더 이름 수정
     */
    @PATCH(PATCH_FOLDER_NAME)
    Call<Void> requestUpdateFolderName(@Path("folder_id") int folderId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body FolderNameEditRequest folderNameEditRequest);

    /**
     * 폴더 추가
     */
    @POST(POST_FOLDER)
    Call<Void> requestInsertFolder(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Nullable @Body FolderAddRequest folderAddRequest);

}
