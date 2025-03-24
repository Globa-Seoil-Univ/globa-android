package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.DictionaryApiEndpoint.GET_DICTIONARY;
import static team.y2k2.globa.api.endpoints.DictionaryApiEndpoint.POST_DICTIONARY;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.response.KeywordDetailResponse;

public interface DictionaryApiService {
    /**
     * 단어 조회
     */
    @GET(GET_DICTIONARY)
    Call<KeywordDetailResponse> searchDictionary(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("keyword") String keyword);

    /**
     * 단어 추가 (Todo - 작업 필요)
     */
    @POST(POST_DICTIONARY)
    Call<KeywordDetailResponse> insertDictionary(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("keyword") String keyword);

}
