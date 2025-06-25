package team.y2k2.globa.api.services;

import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.DELETE_RECORD;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_ALL;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_ANALYSIS_IN_FOLDER;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_DETAIL_IN_FOLDER;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_IN_FOLDER;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_QUIZ;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_RECEIVING;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_SEARCH;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.GET_RECORD_SHARING;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.PATCH_RECORD_MOVE_FOLDER;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.PATCH_RECORD_NAME;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.PATCH_RECORD_STUDY;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.POST_RECORD;
import static team.y2k2.globa.api.endpoints.RecordApiEndpoint.POST_RECORD_QUIZ;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.DocsNameEditRequest;
import team.y2k2.globa.api.model.request.QuizResultRequest;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.request.StudyTimeRequest;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.api.model.response.StatisticsResponse;

public interface RecordApiService {

    /*
     * Record - 음성 관련 API
     */
//    @DELETE(DELETE_RECORD_SHARE_LINK); 문서 링크 공유 취소

    /**
     * 문서 삭제
     */
    @DELETE(DELETE_RECORD)
    Call<Void> requestDeleteRecord(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 문서 추가
     */
    @POST(POST_RECORD)
    Call<Void> requestCreateRecord(@Path("folderId") String folderId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body RecordCreateRequest insertDocumentRequest);

    /**
     * 폴더 내 녹음 파일 조회
     */
    @GET(GET_RECORD_IN_FOLDER)
    Call<FolderInsideRecordResponse> requestGetFolderInside(@Path("folderId") int folderId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("page") int page, @Query("count") int count);

    /**
     * 퀴즈 조회
     */
    @GET(GET_RECORD_QUIZ)
    Call<List<Quiz>> requestGetQuiz(@Path("folderId") int folderId, @Path("recordId") int recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 모든 녹음 파일 조회
     */
    @GET(GET_RECORD_ALL)
    Call<RecordResponse> requestGetRecords(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("count") int number);

    /**
     * 공유 하는 문서 조회
     */
    @GET(GET_RECORD_SHARING)
    Call<RecordResponse> requestGetRecordsOfSharing(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("count") int number);

    /**
     * 문서 검색
     */
    @GET(GET_RECORD_SEARCH)
    Call<SearchResponse> searchRecordForKeyword(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("keyword") String keyword, @Query("page") int page, @Query("count") int count);

    /**
     * 공유 받는 문서 조회
     */
    @GET(GET_RECORD_RECEIVING)
    Call<RecordResponse> requestGetRecordsOfReceiving(@Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Query("count") int number);

    /**
     * 녹음 파일 상세 조회
     */
    @GET(GET_RECORD_DETAIL_IN_FOLDER)
    Call<DocsDetailResponse> requestGetDocumentDetail(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 문서 내 시각화 자료 조회
     */
    @GET(GET_RECORD_ANALYSIS_IN_FOLDER)
    Call<StatisticsResponse> requestDocStatistics(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

    /**
     * 공부시간 수정
     */
    @PATCH(PATCH_RECORD_STUDY)
    Call<Void> requestStudyTime(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body StudyTimeRequest studyTimeRequest);

    /**
     * 문서 이름 수정
     */
    @PATCH(PATCH_RECORD_NAME)
    Call<Void> requestUpdateRecordName(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body DocsNameEditRequest docsNameEditRequest);

    /**
     * 문서 폴더 이동
     */
    @PATCH(PATCH_RECORD_MOVE_FOLDER)
    Call<Void> requestUpdateDocsMove(@Path("folderId") String folderId, @Path("recordId") String recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body DocsMoveRequest docsMoveRequest);

    /**
     * 퀴즈 결과 추가
     */
    @POST(POST_RECORD_QUIZ)
    Call<Void> requestInsertQuizResult(@Path("folderId") int folderId, @Path("recordId") int recordId, @Header("Content-Type") String contentType, @Header("Authorization") String authorization, @Body QuizResultRequest result);

}
