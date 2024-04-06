package team.y2k2.globa.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import team.y2k2.globa.jwt.TokenRequestModel;
import team.y2k2.globa.jwt.TokenResponseModel;
import team.y2k2.globa.login.LoginModel;

public interface ApiService {

    String API_BASE_URL = "";

    @POST("/auth")
    Call<TokenResponseModel> sendRefreshToken(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body TokenRequestModel refreshToken
    );


    @POST("/user")
    Call<LoginModel> sendLogin(
            @Body LoginModel requestBody
    );

    @GET("/user")
    Call<Void> searchUser();



}
