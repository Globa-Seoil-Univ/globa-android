package team.y2k2.globa.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/health")
    Call<ResponseBody> healthCheck();
}
