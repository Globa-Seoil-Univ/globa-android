package team.y2k2.globa.api;

import static team.y2k2.globa.api.ApiModel.*;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.model.request.DocsMoveRequest;
import team.y2k2.globa.api.model.request.FirstCommentRequest;
import team.y2k2.globa.api.model.request.FolderAddRequest;
import team.y2k2.globa.api.model.request.LoginRequest;
import team.y2k2.globa.api.model.request.RecordCreateRequest;
import team.y2k2.globa.api.model.response.CommentResponse;
import team.y2k2.globa.api.model.response.DocsDetailResponse;
import team.y2k2.globa.api.model.response.FolderInsideRecordResponse;
import team.y2k2.globa.api.model.response.FolderResponse;
import team.y2k2.globa.api.model.response.LoginResponse;
import team.y2k2.globa.api.model.response.NoticeResponse;
import team.y2k2.globa.api.model.response.RecordResponse;
import team.y2k2.globa.api.model.response.UserInfoResponse;
import team.y2k2.globa.docs.edit.DocsNameEditRequest;

public class ApiClient {
    public static ApiService apiService;
    public static String authorization;

    public ApiClient(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        authorization = "Bearer " + preferences.getString("accessToken", "");
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

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param count : 가져올 문서 개수
     */
    public RecordResponse requestGetRecords(int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<RecordResponse> call = apiService.requestGetRecords(APPLICATION_JSON, authorization, count);
                Response<RecordResponse> response = null;

                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoginResponse requestSignIn(LoginRequest request){
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<LoginResponse> call = apiService.requestSignIn(request);
                Response<LoginResponse> response;

                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Response<Void> requestInsertFolder(String title) {
        FolderAddRequest request = new FolderAddRequest(title);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestInsertFolder(APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response<Void> requestUpdateRecordName(String folderId, String recordId, String title) {
        DocsNameEditRequest request = new DocsNameEditRequest(title);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestUpdateRecordName(folderId, recordId, APPLICATION_JSON, authorization, request);

                Response<Void> response;

                try {
                    response = call.execute();
                    Log.d("문서 수정", "응답 코드: " + response.code());
                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FolderResponse> requestGetFolders(int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<List<FolderResponse>> call = apiService.requestGetFolders(APPLICATION_JSON, authorization, page, count);
                Response<List<FolderResponse>> response;

                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FolderInsideRecordResponse requestGetFolderInside(int folderId, int page, int count) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<FolderInsideRecordResponse> call = apiService.requestGetFolderInside(folderId,APPLICATION_JSON, authorization, page, count);
                Response<FolderInsideRecordResponse> response;

                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<Void> requestCreateRecord(String folderId, String title, String path, String size) {
        RecordCreateRequest request = new RecordCreateRequest(title, path, size);

        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestCreateRecord(folderId,APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfoResponse requestUserInfo(){
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<UserInfoResponse> call = apiService.requestUserInfo(APPLICATION_JSON, authorization);
                Response<UserInfoResponse> response;
                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<Void> requestInsertFirstComment(String folderId, String recordId, String sectionId, String startIdx, String endIdx, String content) {
        FirstCommentRequest request = new FirstCommentRequest(startIdx, endIdx, content);
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.requestInsertFirstComment(folderId, recordId, sectionId, APPLICATION_JSON, authorization, request);

                Response<Void> response;
                try {
                    response = call.execute();

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CommentResponse getComments(String folderId, String recordId, String sectionId, String highlightId, int page, int count){
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<CommentResponse> call = apiService.getComments(folderId, recordId, sectionId, highlightId, APPLICATION_JSON, authorization, page, count);
                Response<CommentResponse> response;
                try {
                    response = call.execute();

                    if(response.isSuccessful()) {
                        return response.body();
                    }
                    else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response<Void> deleteComment(String folderId, String recordId, String sectionId, String highlightId, String commentId) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                // 백그라운드 스레드에서 작업을 수행하는 코드
                Call<Void> call = apiService.deleteComment(folderId, recordId, sectionId, highlightId, commentId, APPLICATION_JSON, authorization);

                Response<Void> response;
                try {
                    response = call.execute();

                    switch (response.code()) {
                        case 40110: {
                            response = Response.error(40110, ResponseBody.create(null, "유효하지 않은 토큰")); break;
                        }
                        case 40120: {
                            response = Response.error(40120, ResponseBody.create(null, "만료된 토큰")); break;
                        }
                        case 500: {
                            response = Response.error(500, ResponseBody.create(null, "서버 에러")); break;
                        }
                    }
                } catch (IOException e) {
                    response = Response.error(500, ResponseBody.create(null, "IOException: " + e.getMessage()));
                    e.printStackTrace();
                }
                return response;
            }).get(); // CompletableFuture의 결과를 동기적으로 받아옴
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void handleErrorCode(int code) {
        switch (code) {
            case 40110:
                throw new IllegalArgumentException("유효하지 않은 토큰");
            case 40120:
                throw new IllegalArgumentException("만료된 토큰");
            case 500:
                throw new IllegalStateException("서버 에러");
            default:
                throw new IllegalStateException("알 수 없는 에러: " + code);
        }
    }


}
