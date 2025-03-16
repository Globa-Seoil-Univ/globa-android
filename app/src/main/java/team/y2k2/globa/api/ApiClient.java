package team.y2k2.globa.api;

import static team.y2k2.globa.api.ApiModel.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.model.entity.ShareTarget;
import team.y2k2.globa.api.model.request.CommentRequest;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.FirstCommentRequest;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.request.FolderDeleteRequest;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.request.StudyTimeRequest;
import team.y2k2.globa.api.model.request.SubCommentRequest;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.SearchResponse;
import team.y2k2.globa.api.model.response.SubCommentResponse;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.api.model.request.DocsNameEditRequest;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.login.LoginActivity;

public class ApiClient {
    public static ApiService apiService;
    public static String authorization;
    private final Context context;

    public ApiClient(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        authorization = "Bearer " + preferences.getString("accessToken", "");
        apiService = getApiService();
    }

    public ApiClient(String accessToken) {
        context = null;
        authorization = "Bearer " + accessToken;
        apiService = getApiService();
    }

    public static ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }

    /**
     * @param count : 가져올 공지사항 개수
     */
    public List<NoticeResponse> requestPromotion(int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<List<NoticeResponse>> call = apiService.requestPromotion(APPLICATION_JSON, authorization, count);
                Response<List<NoticeResponse>> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    /**
     * @param count : 가져올 문서 개수
     */
    public RecordResponse requestGetRecords(int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<RecordResponse> call = apiService.requestGetRecords(APPLICATION_JSON, authorization, count);
                Response<RecordResponse> response;
                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public RecordResponse requestGetRecordsOfReceiving(int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<RecordResponse> call = apiService.requestGetRecordsOfReceiving(APPLICATION_JSON, authorization, count);
                Response<RecordResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public RecordResponse requestGetRecordsOfSharing(int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<RecordResponse> call = apiService.requestGetRecordsOfSharing(APPLICATION_JSON, authorization, count);
                Response<RecordResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public TokenResponse requestToken(TokenRequest request) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<TokenResponse> call = apiService.getRequestToken(APPLICATION_JSON, authorization, request);
                Response<TokenResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            copyToClipboard(context, e.getMessage() + request.getRefreshToken());
            return null;
        }
    }

    // 클립보드 복사 메소드
    public void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard != null) {
            ClipData clip = ClipData.newPlainText("code", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public LoginResponse requestSignIn(LoginRequest request) {
        try {

            AtomicBoolean isSuccess = new AtomicBoolean(true);

            LoginResponse parentResponse = CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<LoginResponse> call = apiService.requestSignIn(request);
                Response<LoginResponse> response;

                try {
                    response = call.execute();

                    if (!response.isSuccessful() || response.code() == 400) {
                        handleErrorCode(response.code());
                        isSuccess.set(false);
                    }
                    return response.body();
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴

            if (!isSuccess.get())
                parentResponse = null;
            return parentResponse;
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }


    // 폴더 추가
    public Response<Void> requestInsertFolder(String title, List<ShareTarget> shareTargets) {
        FolderAddRequest request = new FolderAddRequest(title, shareTargets);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestInsertFolder(APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            // CompletableFuture 실행 중 에러 발생 시 에러 처리
            handleErrorCode(500);
            return Response.error(500, ResponseBody.create(null, ""));
        }
    }

    // 문서 이름 업데이트
    public Response<Void> requestUpdateRecordName(String folderId, String recordId, String title) {
        DocsNameEditRequest request = new DocsNameEditRequest(title);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestUpdateRecordName(folderId, recordId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    if(!handleErrorCode(response.code())) {
                        return response;
                    }
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public Response<Void> requestDeleteFolder(int folderId) {
        FolderDeleteRequest request = new FolderDeleteRequest(folderId);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestDeleteFolder(folderId, APPLICATION_JSON, authorization);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }

                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public FolderResponse requestGetFolders(int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<FolderResponse> call = apiService.requestGetFolders(APPLICATION_JSON, authorization, page, count);
                Response<FolderResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public Response<Void> requestUpdateDocsMove(String folderId, String recordId, String targetFolderId) {
        DocsMoveRequest request = new DocsMoveRequest(String.valueOf(targetFolderId));

        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestUpdateDocsMove(folderId, recordId, APPLICATION_JSON, authorization, request);
                Response<Void> response;

                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public FolderInsideRecordResponse requestGetFolderInside(int folderId, int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<FolderInsideRecordResponse> call = apiService.requestGetFolderInside(folderId, APPLICATION_JSON, authorization, page, count);
                Response<FolderInsideRecordResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public Response<Void> requestCreateRecord(String folderId, String title, String path, String size) {
        RecordCreateRequest request = new RecordCreateRequest(title, path, size);

        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestCreateRecord(folderId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public Response<Void> requestAcceptShareInvite(String folderId, String shareId) {

        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestAcceptShareInvite(folderId, shareId, APPLICATION_JSON, authorization);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public UserInfoResponse requestUserInfo() {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<UserInfoResponse> call = apiService.requestUserInfo(APPLICATION_JSON, authorization);
                Response<UserInfoResponse> response;
                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }


    public DocsDetailResponse requestGetDocumentDetail(String folderId, String recordId) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<DocsDetailResponse> call = apiService.requestGetDocumentDetail(folderId, recordId, APPLICATION_JSON, authorization);
                Response<DocsDetailResponse> response;
                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 댓글 추가 (최초X)
    public Response<Void> requestInsertComment(String folderId, String recordId, String sectionId, String highlightId, String content) {
        CommentRequest request = new CommentRequest(content);
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<Void> call = apiService.requestInsertComment(folderId, recordId, sectionId, highlightId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 대댓글 추가
    public Response<Void> requestInsertSubComment(String folderId, String recordId, String sectionId, String highlightId, String parentId, String content) {
        SubCommentRequest request = new SubCommentRequest(content);
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<Void> call = apiService.requestInsertSubComment(folderId, recordId, sectionId, highlightId, parentId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 댓글 최초 추가
    public Response<Void> requestInsertFirstComment(String folderId, String recordId, String sectionId, String startIdx, String endIdx, String content) {
        FirstCommentRequest request = new FirstCommentRequest(startIdx, endIdx, content);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestInsertFirstComment(folderId, recordId, sectionId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            Log.d("댓글 최초 추가 API", "댓글 최초 추가 InterruptedException/ExecutionException 오류 e: " + e.getMessage());
        }
        return null;
    }

    // 댓글 가져오기
    public CommentResponse getComments(String folderId, String recordId, String sectionId, String highlightId, int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<CommentResponse> call = apiService.getComments(folderId, recordId, sectionId, highlightId, APPLICATION_JSON, authorization, page, count);
                Response<CommentResponse> response;
                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        Log.d("댓글 가져오기 API", "댓글 가져오기 API 응답 코드: " + response.code());
                        return response.body();
                    } else {
                        Log.d("댓글 가져오기 API", "댓글 가져오기 API 응답 코드: " + response.code());
                        handleErrorCode(response.code());
                        return null;
                    }

                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 대댓글 가져오기
    public SubCommentResponse getSubComments(String folderId, String recordId, String sectionId, String highlightId, String parentId, int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                Call<SubCommentResponse> call = apiService.getSubComments(folderId, recordId, sectionId, highlightId, parentId, APPLICATION_JSON, authorization, page, count);
                Response<SubCommentResponse> response;
                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        Log.d("대댓글 가져오기", "대댓글 가져오기 Code: " + response.code());
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        Log.d("대댓글 가져오기", "대댓글 가져오기 Code: " + response.code());
                        return null;
                    }
                } catch (IOException e) {
                    Log.d("대댓글 가져오기", "ApiClient IOException 발생");
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 댓글 삭제
    public Response<Void> deleteComment(String folderId, String recordId, String sectionId, String highlightId, String commentId) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.deleteComment(folderId, recordId, sectionId, highlightId, commentId, APPLICATION_JSON, authorization);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 댓글 수정
    public Response<Void> updateComment(String folderId, String recordId, String sectionId, String highlightId, String commentId, String text) {
        CommentRequest commentRequest = new CommentRequest(text);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.updateComment(folderId, recordId, sectionId, highlightId, commentId, APPLICATION_JSON, authorization, commentRequest);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 알림 가져오기
    public NotificationResponse requestNotification(String type) {
        try {
            return CompletableFuture.supplyAsync(() -> {

                Call<NotificationResponse> call = apiService.requestGetNotification(APPLICATION_JSON, authorization, 1, 100, type);
                Response<NotificationResponse> response;
                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        Log.d(getClass().getSimpleName(), "알림 가져오기 성공: " + response.code());
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        Log.d(getClass().getSimpleName(), "알림 가져오기 실패: " + response.code());
                        return null;
                    }

                } catch (IOException e) {
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(getClass().getSimpleName(), "알림 오류: " + e.getMessage());
            return null;
        }
    }

    // 공부시간 수정
    public Response<Void> updateStudyTime(String folderId, String recordId, String studyTime) {
        StudyTimeRequest studyTimeRequest = new StudyTimeRequest(studyTime);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestStudyTime(folderId, recordId, APPLICATION_JSON, authorization, studyTimeRequest);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get();
        }catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public SearchResponse searchForKeyword(String keyword) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<SearchResponse> call = apiService.searchRecordForKeyword(APPLICATION_JSON, authorization, keyword, 1, 20);
                Response<SearchResponse> response;

                try {
                    response = call.execute();

                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 댓글 삭제
    public Response<Void> deleteRecord(String folderId, String recordId) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestDeleteRecord(folderId, recordId, APPLICATION_JSON, authorization);

                Response<Void> response;
                try {
                    response = call.execute();
                    handleErrorCode(response.code());
                } catch (IOException e) {
                    // IOException 발생 시 에러 처리
                    handleErrorCode(500);
                    response = Response.error(500, ResponseBody.create(null, ""));
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public boolean handleErrorCode(int code) {
        Intent intent;
        switch (code) {
            // 400 Bad Request 관련 에러: 잘못된 요청, 필요한 인자 누락 등
            case ERR_BAD_REQUEST: {
                intent = new Intent(context, IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true; // IntroActivity로 이동했으므로 true 반환
            }
            case ERR_EXPIRED_ACCESS_TOKEN: {
                SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
                String refreshToken = preferences.getString("refreshToken", "");
                String accessToken = preferences.getString("accessToken", "");

                if (!refreshToken.equalsIgnoreCase("") && !accessToken.equalsIgnoreCase("")) {
                    // TokenRequest, TokenResponse, requestToken()은 기존 코드에서 정의된 대로 사용
                    TokenRequest request = new TokenRequest(refreshToken);
                    TokenResponse response = requestToken(request);

                    if (response != null) { // response가 null이 아닌지 확인
                        String newAccessToken = response.getAccessToken();
                        String newRefreshToken = response.getRefreshToken();

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", newAccessToken);
                        editor.putString("refreshToken", newRefreshToken);
                        editor.apply(); // commit() 대신 apply() 사용
                        return true; // 토큰 갱신 성공
                    } else {
                        showErrorDialog(code, "토큰 갱신에 실패했습니다.");
                        return false; // 토큰 갱신 실패
                    }
                }
                return false; // refreshToken이나 accessToken이 없는 경우
            }
            case ERR_ACTIVE_REFRESH_TOKEN:
            case ERR_NOT_MATCH_REFRESH_TOKEN:
            case ERR_REQUIRED_ACCESS_TOKEN:
            case ERR_REQUIRED_REQUEST_TOKEN:
            case ERR_REQUIRED_USER_CODE:
            case ERR_REQUIRED_USER_ID:
            case ERR_FOLDER_DELETE_BAD_REQUEST:
            case ERR_DELETED_USER:
            case ERR_REQUIRED_FOLDER_TITLE:
            case ERR_REQUIRED_FOLDER_ID:
            case ERR_REQUIRED_QUIZ_ID:
            case ERR_REQUIRED_RECORD_ID:
                showErrorDialog(code, "잘못된 요청 또는 필요한 정보가 누락되었습니다.");
                return false;
            // 400 Bad Request 관련 에러 (추가): 잘못된 요청, 필요한 정보 누락 등 (post 관련)
            case ERR_REQUIRED_QUIZ:
            case ERR_RECORD_POST_BAD_REQUEST:
            case ERR_REQUIRED_RECORD_TITLE:
            case ERR_REQUIRED_MOVE_ARRIVED_ID:
            case ERR_INVITE_BAD_REQUEST:
            case ERR_INVITE_ACCEPT_BAD_REQUEST:
            case ERR_REQUIRED_NOTICE_ID:
            case ERR_NOFI_POST_BAD_REQUEST:
            case ERR_SURVEY_POST_BAD_REQUEST:
            case ERR_NOFI_TYPE_BAD_REQUEST:
            case ERR_REQUIRED_NOTIFICATION_ID:
            case ERR_NOT_PARENT_COMMENT:
                showErrorDialog(code, "잘못된 요청 또는 필요한 정보가 누락되었습니다. (post 관련)");
                return false;

            // 401 Unauthorized 관련 에러: 인증 실패, 잘못된 토큰
            case ERR_UNAUTHORIZED:
            case ERR_INVALID_TOKEN:
            case ERR_SIGNATURE:
            case ERR_EXPIRED_REFRESH_TOKEN:
                // LoginActivity를 시작하고 현재 액티비티 스택을 모두 제거
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return false;

            // 403 Forbidden 관련 에러: 권한 없음, 잘못된 접근
            case ERR_FORBIDDEN:
            case ERR_NOT_NULL_ROLE:
            case ERR_NOT_DESERVE_MODIFY_INVITATION:
            case ERR_NOT_DESERVE_DICTIONARY:
            case ERR_MISMATCH_INQUIRY_OWNER:
            case ERR_MISMATCH_FOLDER_OWNER:
                showErrorDialog(code, "권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_ADD_NOTICE:
                showErrorDialog(code, "공지사항 작성 권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_ACCESS_FOLDER:
                showErrorDialog(code, "폴더 접근 권한이 없습니다.");
                return false;

            case ERR_NOT_DESERVE_POST_COMMENT:
                showErrorDialog(code, "댓글 작성 권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_FCM:
                showErrorDialog(code, "알림을 보낼 수 없습니다.");
                return false;
            case ERR_INVALID_TOKEN_USER:
                showErrorDialog(code, "토큰이 일치하지 않습니다.");
                return false;
            case ERR_MISMATCH_COMMENT_OWNER:
            case ERR_MISMATCH_NOFI_OWNER:
            case ERR_MISMATCH_ANALYSIS_OWNER:
            case ERR_MISMATCH_RENAME_OWNER:
            case ERR_MISMATCH_QUIZ_RECORD_ID:
            case ERR_MISMATCH_RECORD_OWNER:
            case ERR_MISMATCH_RECORD_FOLDER:
            case ERR_MISMATCH_NOTIFICATION_OWNER:
                showErrorDialog(code, "권한이 없습니다.");
                return false;

            // 404 Not Found 관련 에러: 자원을 찾을 수 없음
            case ERR_NOT_FOUND:
                showErrorDialog(code, "서버가 응답하지 않습니다.");
                return false;
            case ERR_NOT_FOUND_USER:
                showErrorDialog(code, "찾을 수 없는 유저입니다.");
                return false;
            case ERR_NOT_FOUND_DEFAULT_FOLDER:
                showErrorDialog(code, "기본 폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_TARGET_USER:
                showErrorDialog(code, "유저를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_INQUIRY:
                showErrorDialog(code, "문의를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_NOTICE:
                showErrorDialog(code, "공지를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_NOTIFICATION:
                showErrorDialog(code, "알림을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ANSWER:
                showErrorDialog(code, "답변을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_FOLDER:
            case ERR_NOT_FOUND_ORIGIN_FOLDER:
            case ERR_NOT_FOUND_TARGET_FOLDER:
            case ERR_NOT_FOUND_FOLDER_FIREBASE:
                showErrorDialog(code, "폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ACCESSIBLE_FOLDER:
                showErrorDialog(code, "접근 가능한 폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_SHARE:
                showErrorDialog(code, "공유 정보를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_HIGHLIGHT:
                showErrorDialog(code, "하이라이트를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_PARENT_COMMENT:
                showErrorDialog(code, "댓글을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_RECORD:
                showErrorDialog(code, "문서를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ANALYSIS:
                showErrorDialog(code, "통계를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_QUIZ:
                showErrorDialog(code, "퀴즈를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_RECORD_FIREBASE:
                showErrorDialog(code, "음성 파일을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_SECTION:
                showErrorDialog(code, "섹션을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_COMMENT:
                showErrorDialog(code, "해당 자원을 찾을 수 없습니다.");
                return false;

            // 409 Conflict 관련 에러: 중복된 데이터 등의 충돌 발생
            case ERR_DUPLICATED:
            case ERR_HIGHLIGHT_DUPLICATED:
            case ERR_FOLDER_NAME_DUPLICATED:
            case ERR_INQUIRY_ANSWER_DUPLICATED:
            case ERR_SHARE_USER_DUPLICATED:
            case ERR_NOTIFICATION_READ_DUPLICATED:
                showErrorDialog(code, "중복된 데이터 또는 충돌이 발생했습니다.");
                return false;

            // 500 Internal Server Error 관련 에러: 서버 내부 오류
            case ERR_INTERNAL_SERVER_ERROR:
            case ERR_FAILED_FILE_UPLOAD:
            case ERR_INTERNAL_SERVER_ERROR_50020:
            case ERR_REDIS_TIMEOUT:
            case ERR_FAILED_FOLDER_CREATE:
            case ERR_FAILED_FOLDER_DELETE:
            case ERR_FAILED_FIREBASE:
            case ERR_FAILED_EXCEL:
            case ERR_NOT_FOUND_KEYWORD_EXCEL:
                showErrorDialog(code, "서버 내부 오류가 발생했습니다.");
                return false;

            default:
                showErrorDialog(code, "알 수 없는 에러가 발생했습니다.");
                return false;
        }
    }

    // ErrorHandler 클래스 수정 (showErrorDialog 메서드)
    private void showErrorDialog(int errorCode, String errorMessage) {
        if (context == null || !(context instanceof Activity)) {
            return;
        }

        if(! ((Activity) context).isFinishing()) {
            ((Activity) context).runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("에러 발생")
                        .setMessage("에러 코드: " + errorCode + "\n" + errorMessage)
                        .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                        .setCancelable(false)
                        .show();
            });
        }
    }
}