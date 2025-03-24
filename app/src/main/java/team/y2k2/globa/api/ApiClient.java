package team.y2k2.globa.api;

import static team.y2k2.globa.api.ApiModel.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.login.LoginActivity;

public class ApiClient {
    public UserApiClient userApiClient;
    private static ApiClient apiClient;

    public static Retrofit retrofit; // Retrofit instance

    protected final Context context;

    protected ApiClient(Context context) {
        this.context = context.getApplicationContext();
        initializeRetrofit();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (apiClient == null) {
            apiClient = new ApiClient(context);
        }
        return apiClient;
    }

    public Context getContext() {
        return context;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.219.111").addConverterFactory(GsonConverterFactory.create()).build();
    }

    public String getAuthorization() {
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        return "Bearer " + preferences.getString("accessToken", "");
    }

    // 공통 API 호출 처리 메서드
    public <T> T executeApiCall(Call<T> call) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Response<T> response = call.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        handleErrorCode(response.code());
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // 공통 Void API 호출 처리 메서드
    public Response<Void> executeVoidApiCall(Call<Void> call) {
        try {
            CompletableFuture<Response<Void>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Response<Void> response = call.execute();
                    handleErrorCode(response.code());
                    return response;
                } catch (IOException e) {
                    handleErrorCode(500);
                    return Response.error(500, ResponseBody.create(null, ""));
                }
            });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public boolean handleErrorCode(int code) {
        Intent intent;
        userApiClient = new UserApiClient();

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
                    TokenResponse response = userApiClient.requestToken(request);

                    if (response != null) { // response가 null이 아닌지 확인
                        String newAccessToken = response.getAccessToken();
                        String newRefreshToken = response.getRefreshToken();

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", newAccessToken);
                        editor.putString("refreshToken", newRefreshToken);
                        editor.apply(); // commit() 대신 apply() 사용
                        return true; // 토큰 갱신 성공
                    } else {
                        displayErrorDialog(code, "토큰 갱신에 실패했습니다.");
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
                displayErrorDialog(code, "잘못된 요청 또는 필요한 정보가 누락되었습니다.");
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
                displayErrorDialog(code, "잘못된 요청 또는 필요한 정보가 누락되었습니다. (post 관련)");
                return false;

            // 401 Unauthorized 관련 에러: 인증 실패, 잘못된 토큰
            case ERR_UNAUTHORIZED:
            case ERR_INVALID_TOKEN:
            case ERR_SIGNATURE:
            case ERR_EXPIRED_REFRESH_TOKEN:
                // LoginActivity를 시작하고 현재 액티비티 스택을 모두 제거
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("expired", true);
                context.startActivity(intent);
                return false;

            // 403 Forbidden 관련 에러: 권한 없음, 잘못된 접근
            case ERR_FORBIDDEN:
            case ERR_NOT_NULL_ROLE:
            case ERR_NOT_DESERVE_MODIFY_INVITATION:
            case ERR_NOT_DESERVE_DICTIONARY:
            case ERR_MISMATCH_INQUIRY_OWNER:
            case ERR_MISMATCH_FOLDER_OWNER:
                displayErrorDialog(code, "권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_ADD_NOTICE:
                displayErrorDialog(code, "공지사항 작성 권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_ACCESS_FOLDER:
                displayErrorDialog(code, "폴더 접근 권한이 없습니다.");
                return false;

            case ERR_NOT_DESERVE_POST_COMMENT:
                displayErrorDialog(code, "댓글 작성 권한이 없습니다.");
                return false;
            case ERR_NOT_DESERVE_FCM:
                displayErrorDialog(code, "알림을 보낼 수 없습니다.");
                return false;
            case ERR_INVALID_TOKEN_USER:
                displayErrorDialog(code, "토큰이 일치하지 않습니다.");
                return false;
            case ERR_MISMATCH_COMMENT_OWNER:
            case ERR_MISMATCH_NOFI_OWNER:
            case ERR_MISMATCH_ANALYSIS_OWNER:
            case ERR_MISMATCH_RENAME_OWNER:
            case ERR_MISMATCH_QUIZ_RECORD_ID:
            case ERR_MISMATCH_RECORD_OWNER:
            case ERR_MISMATCH_RECORD_FOLDER:
            case ERR_MISMATCH_NOTIFICATION_OWNER:
                displayErrorDialog(code, "권한이 없습니다.");
                return false;

            // 404 Not Found 관련 에러: 자원을 찾을 수 없음
            case ERR_NOT_FOUND:
                displayErrorDialog(code, "서버가 응답하지 않습니다.");
                return false;
            case ERR_NOT_FOUND_USER:
                displayErrorDialog(code, "찾을 수 없는 유저입니다.");
                return false;
            case ERR_NOT_FOUND_DEFAULT_FOLDER:
                displayErrorDialog(code, "기본 폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_TARGET_USER:
                displayErrorDialog(code, "유저를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_INQUIRY:
                displayErrorDialog(code, "문의를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_NOTICE:
                displayErrorDialog(code, "공지를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_NOTIFICATION:
                displayErrorDialog(code, "알림을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ANSWER:
                displayErrorDialog(code, "답변을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_FOLDER:
            case ERR_NOT_FOUND_ORIGIN_FOLDER:
            case ERR_NOT_FOUND_TARGET_FOLDER:
            case ERR_NOT_FOUND_FOLDER_FIREBASE:
                displayErrorDialog(code, "폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ACCESSIBLE_FOLDER:
                displayErrorDialog(code, "접근 가능한 폴더를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_SHARE:
                displayErrorDialog(code, "공유 정보를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_HIGHLIGHT:
                displayErrorDialog(code, "하이라이트를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_PARENT_COMMENT:
                displayErrorDialog(code, "댓글을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_RECORD:
                displayErrorDialog(code, "문서를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_ANALYSIS:
                displayErrorDialog(code, "통계를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_QUIZ:
                displayErrorDialog(code, "퀴즈를 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_RECORD_FIREBASE:
                displayErrorDialog(code, "음성 파일을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_SECTION:
                displayErrorDialog(code, "섹션을 찾을 수 없습니다.");
                return false;
            case ERR_NOT_FOUND_COMMENT:
                displayErrorDialog(code, "해당 자원을 찾을 수 없습니다.");
                return false;

            // 409 Conflict 관련 에러: 중복된 데이터 등의 충돌 발생
            case ERR_DUPLICATED:
            case ERR_HIGHLIGHT_DUPLICATED:
            case ERR_FOLDER_NAME_DUPLICATED:
            case ERR_INQUIRY_ANSWER_DUPLICATED:
            case ERR_SHARE_USER_DUPLICATED:
            case ERR_NOTIFICATION_READ_DUPLICATED:
                displayErrorDialog(code, "중복된 데이터 또는 충돌이 발생했습니다.");
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
                displayErrorDialog(code, "서버 내부 오류가 발생했습니다.");
                return false;
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
                return true;

            default:
                displayErrorDialog(code, "알 수 없는 에러가 발생했습니다.");
                return false;
        }
    }

    // 에러 다이얼로그(모달) 처리 프로세스.
    // 각 기능에 맞게 다른 에러 화면을 호출하고자 하면 오버라이딩하여 사용
    public void displayErrorDialog(int errorCode, String errorMessage) {
        if (context == null || !(context instanceof Activity)) {
            return;
        }

        if (!((Activity) context).isFinishing()) {
            ((Activity) context).runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("에러 발생").setMessage("에러 코드: " + errorCode + "\n" + errorMessage).setPositiveButton("확인", (dialog, which) -> dialog.dismiss()).setCancelable(false).show();
            });
        }
    }
}