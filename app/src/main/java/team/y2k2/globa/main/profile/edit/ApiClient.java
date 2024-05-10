package team.y2k2.globa.main.profile.edit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;

public class ApiClient {

    private static ApiService apiService;

    public static ApiService getApiService() {
        if(apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

}
