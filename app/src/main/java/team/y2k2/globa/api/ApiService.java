package team.y2k2.globa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import team.y2k2.globa.docs.upload.DocsUploadRequestModel;
import team.y2k2.globa.docs.upload.DocsUploadResponseModel;
import team.y2k2.globa.login.LoginResponse;
import team.y2k2.globa.network.jwt.TokenRequestModel;
import team.y2k2.globa.network.jwt.TokenResponseModel;
import team.y2k2.globa.login.LoginRequest;

public interface ApiService {
    String API_BASE_URL_LOCAL = "http://192.168.219.111:8080";
    String API_BASE_URL = "http://globa.tetraplace.com";

    @POST("/auth")
    Call<TokenResponseModel> sendRefreshToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequestModel refreshToken
    );

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/user")
    Call<LoginResponse> sendLogin(@Body LoginRequest requestBody);


    @POST("/folder/{folder_id}/record")
    Call<DocsUploadResponseModel> createRecord(
            @Path("folder_id") String folderId,
            @Body DocsUploadRequestModel requestBody
    );

    @GET("/user")
    Call<Void> searchUser();
}
