package team.y2k2.globa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import team.y2k2.globa.docs.upload.DocsUploadRequestModel;
import team.y2k2.globa.docs.upload.DocsUploadResponseModel;
import team.y2k2.globa.network.jwt.TokenRequestModel;
import team.y2k2.globa.network.jwt.TokenResponseModel;
import team.y2k2.globa.login.LoginModel;

public interface ApiService {
    String API_BASE_URL_LOCAL = "182.228.83.7";
    String API_BASE_URL = "globa.tetraplace.com";

    @POST("/auth")
    Call<TokenResponseModel> sendRefreshToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequestModel refreshToken
    );

    @POST("/user")
    Call<LoginModel> sendLogin(
            @Header("Content-Type") String contentType,
            @Body LoginModel requestBody
    );


    @POST("/folder/{folder_id}/record")
    Call<DocsUploadResponseModel> createRecord(
            @Path("folder_id") String folderId,
            @Body DocsUploadRequestModel requestBody
    );

    @GET("/user")
    Call<Void> searchUser();
}
