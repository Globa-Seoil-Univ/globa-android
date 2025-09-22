package team.y2k2.globa.api.clients;

import static team.y2k2.globa.api.ApiModel.APPLICATION_JSON;

import android.content.Context;

import java.util.List;

import retrofit2.Response;
import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.DocsNameEditRequest;
import team.y2k2.globa.api.model.request.QuizResultRequest;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.request.StudyTimeRequest;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.QuizResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.api.model.response.StatisticsResponse;
import team.y2k2.globa.api.services.RecordApiService;

public class RecordApiClient extends ApiClient {
    private final RecordApiService apiService;

    public RecordApiClient(Context context) {
        super(ApiClient.getInstance(context).getContext());
        apiService = ApiClient.getRetrofit().create(RecordApiService.class);
    }

    // 댓글 삭제
    public Response<?> deleteRecord(String folderId, String recordId) {
        return executeVoidApiCall(apiService.requestDeleteRecord(folderId, recordId, APPLICATION_JSON, getAuthorization()));
    }

    public void requestCreateRecord(String folderId, String title, String path, String lang) {
        executeVoidApiCall(apiService.requestCreateRecord(folderId, APPLICATION_JSON, getAuthorization(), new RecordCreateRequest(title, path, lang)));
    }

    public FolderInsideRecordResponse requestGetFolderInside(int folderId, int page, int count) {
        return executeApiCall(apiService.requestGetFolderInside(folderId, APPLICATION_JSON, getAuthorization(), page, count));
    }

    public RecordResponse requestGetRecords(int count) {
        return executeApiCall(apiService.requestGetRecords(APPLICATION_JSON, getAuthorization(), count));
    }

    public RecordResponse requestGetRecordsOfSharing(int count) {
        return executeApiCall(apiService.requestGetRecordsOfSharing(APPLICATION_JSON, getAuthorization(), count));
    }

    public SearchResponse searchForKeyword(String keyword) {
        return executeApiCall(apiService.searchRecordForKeyword(APPLICATION_JSON, getAuthorization(), keyword, 1, 20));
    }

    public RecordResponse requestGetRecordsOfReceiving(int count) {
        return executeApiCall(apiService.requestGetRecordsOfReceiving(APPLICATION_JSON, getAuthorization(), count));
    }

    public DocsDetailResponse requestGetDocumentDetail(String folderId, String recordId) {
        return executeApiCall(apiService.requestGetDocumentDetail(folderId, recordId, APPLICATION_JSON, getAuthorization()));
    }

    // 문서 시각화
    public StatisticsResponse requestDocsStatistics(String folderId, String recordId) {
        return executeApiCall(apiService.requestDocStatistics(folderId, recordId, APPLICATION_JSON, getAuthorization()));
    }

    // 공부시간 수정
    public void updateStudyTime(String folderId, String recordId, String studyTime) {
        executeVoidApiCall(apiService.requestStudyTime(folderId, recordId, APPLICATION_JSON, getAuthorization(), new StudyTimeRequest(studyTime)));
    }

    // 문서 이름 업데이트
    public Response<?> requestUpdateRecordName(String folderId, String recordId, String title) {
        return executeVoidApiCall(apiService.requestUpdateRecordName(folderId, recordId, APPLICATION_JSON, getAuthorization(), new DocsNameEditRequest(title)));
    }

    public Response<?> requestUpdateDocsMove(String folderId, String recordId, String targetFolderId) {
        return executeVoidApiCall(apiService.requestUpdateDocsMove(folderId, recordId, APPLICATION_JSON, getAuthorization(), new DocsMoveRequest(String.valueOf(targetFolderId))));
    }

    // 퀴즈 불러오기
    public QuizResponse requestGetQuiz(int folderId, int recordId) {
        return executeApiCall(apiService.requestGetQuiz(folderId, recordId, APPLICATION_JSON));
    }

    // 퀴즈 결과 전송
    public void requestInsertQuizResult(int folderId, int recordId, QuizResultRequest quizResults) {
        executeVoidApiCall(apiService.requestInsertQuizResult(folderId, recordId, APPLICATION_JSON, getAuthorization(), quizResults));
    }
}
