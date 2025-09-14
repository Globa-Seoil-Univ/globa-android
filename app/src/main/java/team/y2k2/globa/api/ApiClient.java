package team.y2k2.globa.api;

import static android.content.ContentValues.TAG;
import static team.y2k2.globa.api.ApiModel.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.storage.BuildConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.TokenRequest;
import team.y2k2.globa.api.model.response.TokenResponse;
import team.y2k2.globa.api.services.UserApiService;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.login.LoginActivity;

public class ApiClient {
    public UserApiClient userApiClient;
    private static ApiClient apiClient;

    public static Retrofit retrofit; // Retrofit instance
    public static String BASE_URL = "https://globa.duckdns.org";

    protected final Context context;
    private final SharedPreferences preferences;

    public ApiClient(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        initializeRetrofit();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (apiClient == null) {
            apiClient = new ApiClient(context);
        }
        return apiClient;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public Context getContext() {
        return context;
    }

    public String getAuthorization() {
        SharedPreferences preferences = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        return preferences.getString("accessToken", "");
    }
    private void initializeRetrofit() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);
        }

        clientBuilder.addInterceptor(new AuthInterceptor());
        clientBuilder.authenticator(new TokenAuthenticator());

        OkHttpClient client = clientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private class AuthInterceptor implements Interceptor {
        @NonNull
        @Override
        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
            Request originalRequest = chain.request();

            if (originalRequest.header("Authorization") != null) {
                Log.d(TAG, "AuthInterceptor: 수동으로 추가된 Authorization 헤더를 발견하여, 자동 추가를 건너뜁니다.");
                return chain.proceed(originalRequest);
            }

            String accessToken = preferences.getString("accessToken", null);
            if (accessToken != null && !accessToken.isEmpty()) {
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken);
                Log.d(TAG, "AuthInterceptor: Authorization 헤더가 없어 자동으로 추가합니다.");
                return chain.proceed(requestBuilder.build());
            }

            return chain.proceed(originalRequest);
        }
    }

    private class TokenAuthenticator implements Authenticator {
        @Nullable
        @Override
        public Request authenticate(@Nullable Route route, @NonNull okhttp3.Response response) {
            if (response.code() != 401) {
                return null;
            }
            Log.d(TAG, "TokenAuthenticator: 401 에러 감지. 토큰 갱신을 시도합니다.");

            synchronized (this) {
                String currentToken = preferences.getString("accessToken", "");
                String failedTokenHeader = response.request().header("Authorization");
                String failedToken = (failedTokenHeader != null) ? failedTokenHeader.replace("Bearer ", "") : "";

                if (currentToken != null && !currentToken.equals(failedToken)) {
                    Log.d(TAG, "TokenAuthenticator: 다른 스레드에서 이미 토큰이 갱신되었습니다. 새 토큰으로 재시도합니다.");
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + currentToken)
                            .build();
                }

                String refreshToken = preferences.getString("refreshToken", "");
                if (refreshToken == null || refreshToken.isEmpty()) {
                    Log.e(TAG, "TokenAuthenticator: Refresh Token이 없어 갱신을 중단하고 로그인 화면으로 이동합니다.");
                    navigateToLoginScreen();
                    return null;
                }

                Retrofit refreshRetrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UserApiService refreshService = refreshRetrofit.create(UserApiService.class);

                Log.d(TAG, "TokenAuthenticator: 서버에 새 토큰을 요청합니다...");

                Call<TokenResponse> call = refreshService.getRequestToken(
                        "application/json",
                        new TokenRequest(refreshToken)
                );

                try {
                    retrofit2.Response<TokenResponse> tokenResponse = call.execute();
                    if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                        TokenResponse newTokens = tokenResponse.body();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("accessToken", newTokens.getAccessToken());
                        editor.putString("refreshToken", newTokens.getRefreshToken());
                        editor.commit();

                        Log.i(TAG, "토큰 갱신 성공. 새 토큰으로 원래 요청을 재시도합니다.");
                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + newTokens.getAccessToken())
                                .build();
                    } else {
                        String errorBody = tokenResponse.errorBody() != null ? tokenResponse.errorBody().string() : "No error body";
                        Log.e(TAG, "리프레시 토큰으로 갱신 실패. Status: " + tokenResponse.code() + ", Body: " + errorBody);
                        navigateToLoginScreen();
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "토큰 갱신 중 IOException 발생", e);
                    return null;
                }
            }
        }
    }
    private void navigateToLoginScreen() {
        new android.os.Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "세션 만료. 로그인 화면으로 이동합니다.");
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("expired", true);
            context.startActivity(intent);
        });
    }


    public CompletableFuture<Boolean> isServerOpened() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(BASE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "서버 상태: " + responseCode);
                connection.disconnect();
                return (responseCode >= 200 && responseCode < 503);
            } catch (IOException e) {
                Log.e(TAG, "서버 상태 확인 실패 (IOException): " + e.getMessage());
                return false;
            }
        });
    }

    // 공통 API 호출 처리 메서드
    public <T> T executeApiCall(Call<T> call) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Log.d(TAG, "executeApiCall: 서버와 통신을 시도합니다. URL: " + call.request().url());
                    Response<T> response = call.execute();
                    if (response.isSuccessful()) {
                        Log.i(TAG, "executeApiCall: 통신 성공. Code: " + response.code() + ", URL: " + call.request().url());
                        return response.body();
                    } else {
                        Log.w(TAG, "executeApiCall: 통신 실패. Code: " + response.code() + ", URL: " + call.request().url());
                        if (response.code() != 401) {
                            handleErrorCode(response.code());
                        }
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "API 호출 중 IOException 발생", e);
                    return null;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "API 호출 Future 실행 중 에러 발생", e);
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

    public void handleErrorCode(int code) {
        switch (code) {
            case ERR_UNAUTHORIZED:
            case ERR_INVALID_TOKEN:
            case ERR_SIGNATURE:
            case ERR_EXPIRED_REFRESH_TOKEN:
                Log.e(TAG, "handleErrorCode: 리프레시 토큰 만료(Code: " + code + "). 강제 로그아웃 처리합니다.");
                navigateToLoginScreen();
                return;

            case ERR_BAD_REQUEST:
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
                displayErrorDialog(code, "잘못된 요청 또는 필요한 정보가 누락되었습니다.");
                return;

            case ERR_FORBIDDEN:
            case ERR_NOT_NULL_ROLE:
            case ERR_NOT_DESERVE_MODIFY_INVITATION:
            case ERR_NOT_DESERVE_DICTIONARY:
            case ERR_MISMATCH_INQUIRY_OWNER:
            case ERR_MISMATCH_FOLDER_OWNER:
            case ERR_NOT_DESERVE_ADD_NOTICE:
            case ERR_NOT_DESERVE_ACCESS_FOLDER:
            case ERR_NOT_DESERVE_POST_COMMENT:
            case ERR_NOT_DESERVE_FCM:
            case ERR_INVALID_TOKEN_USER:
            case ERR_MISMATCH_COMMENT_OWNER:
            case ERR_MISMATCH_NOFI_OWNER:
            case ERR_MISMATCH_ANALYSIS_OWNER:
            case ERR_MISMATCH_RENAME_OWNER:
            case ERR_MISMATCH_QUIZ_RECORD_ID:
            case ERR_MISMATCH_RECORD_OWNER:
            case ERR_MISMATCH_RECORD_FOLDER:
            case ERR_MISMATCH_NOTIFICATION_OWNER:
                displayErrorDialog(code, "권한이 없습니다.");
                return;

            case ERR_NOT_FOUND:
            case ERR_NOT_FOUND_USER:
            case ERR_NOT_FOUND_DEFAULT_FOLDER:
            case ERR_NOT_FOUND_TARGET_USER:
            case ERR_NOT_FOUND_INQUIRY:
            case ERR_NOT_FOUND_NOTICE:
            case ERR_NOT_FOUND_NOTIFICATION:
            case ERR_NOT_FOUND_ANSWER:
            case ERR_NOT_FOUND_FOLDER:
            case ERR_NOT_FOUND_ORIGIN_FOLDER:
            case ERR_NOT_FOUND_TARGET_FOLDER:
            case ERR_NOT_FOUND_FOLDER_FIREBASE:
            case ERR_NOT_FOUND_ACCESSIBLE_FOLDER:
            case ERR_NOT_FOUND_SHARE:
            case ERR_NOT_FOUND_HIGHLIGHT:
            case ERR_NOT_FOUND_PARENT_COMMENT:
            case ERR_NOT_FOUND_RECORD:
            case ERR_NOT_FOUND_ANALYSIS:
            case ERR_NOT_FOUND_QUIZ:
            case ERR_NOT_FOUND_RECORD_FIREBASE:
            case ERR_NOT_FOUND_SECTION:
            case ERR_NOT_FOUND_COMMENT:
                displayErrorDialog(code, "요청한 대상을 찾을 수 없습니다.");
                return;

            case ERR_DUPLICATED:
            case ERR_HIGHLIGHT_DUPLICATED:
            case ERR_FOLDER_NAME_DUPLICATED:
            case ERR_INQUIRY_ANSWER_DUPLICATED:
            case ERR_SHARE_USER_DUPLICATED:
            case ERR_NOTIFICATION_READ_DUPLICATED:
                displayErrorDialog(code, "중복된 데이터 또는 충돌이 발생했습니다.");
                return;

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
                return;

            default:
                if (!(code >= 200 && code < 300)) {
                    displayErrorDialog(code, "알 수 없는 에러가 발생했습니다.");
                }
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