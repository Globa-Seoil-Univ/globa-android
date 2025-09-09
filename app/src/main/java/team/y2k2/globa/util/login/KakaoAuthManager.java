package team.y2k2.globa.util.login;

import android.util.Log;

import com.kakao.sdk.auth.model.OAuthToken;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

// 카카오 SDK의 콜백을 안전하게 수신하기 위한 정적 관리 클래스
public class KakaoAuthManager {
    private static final String TAG = "KakaoAuthManager";

    // 결과를 전달할 리스너 (SnsLoginManager의 콜백)
    private static SnsLoginManager.SnsLoginCallback staticCallback;

    // 카카오 SDK에 전달될 정적 콜백 객체
    public static final Function2<OAuthToken, Throwable, Unit> kakaoLoginCallback = (token, error) -> {
        Log.d(TAG, "정적 카카오 콜백이 호출되었습니다.");
        if (staticCallback == null) {
            Log.e(TAG, "콜백을 수신했지만, 결과를 전달할 리스너(staticCallback)가 null입니다. 앱이 완전히 재시작되었을 수 있습니다.");
            return null;
        }

        if (error != null) {
            Log.e(TAG, "카카오 로그인 실패 (정적 콜백)", error);
            staticCallback.onError("Kakao sign-in failed: " + error.getMessage());
        } else if (token != null) {
            Log.i(TAG, "카카오 로그인 성공 (정적 콜백). 토큰을 전달합니다.");
            // LoginModel은 null로 전달하고, access token만 전달하여 SnsLoginManager에서 후속 처리를 하도록 함
            staticCallback.onSuccess(null, token.getAccessToken());
        }

        // 중요: 콜백을 전달한 후에는 반드시 리스너를 null로 만들어 메모리 누수를 방지합니다.
        staticCallback = null;
        return null;
    };

    // SnsLoginManager가 로그인 시도 직전에 자신의 콜백을 등록하는 메소드
    public static void setCallback(SnsLoginManager.SnsLoginCallback callback) {
        Log.d(TAG, "정적 콜백 리스너가 등록되었습니다.");
        staticCallback = callback;
    }
}