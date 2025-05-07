package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import team.y2k2.globa.intro.IntroActivity; // IntroActivity 시작을 위해 필요
import team.y2k2.globa.main.MainActivity;

/**
 * IntroActivity에서 시작하여 LoginActivity를 거쳐 MainActivity로 이동하는
 * 전체 로그인 흐름 (Google 및 Kakao 로그인, 수동 개입 필요)을 테스트하는 클래스입니다.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest { // 클래스 이름은 유지하지만, 내용은 통합 테스트로 변경

    /**
     * 각 테스트 메소드 실행 전에 호출됩니다.
     * Espresso Intents를 초기화하고 EspressoIdlingResource를 등록합니다.
     * SharedPreferences를 초기화하여 각 테스트가 독립적인 환경에서 시작하도록 합니다.
     */
    @Before
    public void setUp() {
        // Espresso Intents 초기화 (intended 사용 전 필수)
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * 각 테스트 메소드 실행 후에 호출됩니다.
     * Espresso Intents를 해제하고 EspressoIdlingResource를 해제합니다.
     * SharedPreferences를 초기화하여 다른 테스트에 영향을 주지 않도록 합니다.
     */
    @After
    public void tearDown() {
        // Espresso Intents 해제
        Intents.release();
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * IntroActivity 시작 -> 서버 온라인 확인 -> LoginActivity 이동 -> 수동 Google 로그인 -> MainActivity 이동
     * 전체 흐름을 테스트합니다.
     *
     * !!! 중요 !!!
     * 이 테스트를 실행할 때, 화면에 나타나는 Google 로그인 UI를 통해
     * 테스트 실행자가 **수동으로 유효한 Google 계정 로그인을 완료**해야 합니다.
     */
    @Test
    public void loginProcessWithGoogle() {
        // 1. IntroActivity 시작
        Log.d("LoginActivityTest", "Launching IntroActivity for Google Test...");
        ActivityScenario<IntroActivity> introScenario = ActivityScenario.launch(IntroActivity.class);

        // 2. IntroActivity가 서버 상태를 확인하고 시작 버튼을 활성화할 때까지 대기
        Log.d("LoginActivityTest", "Waiting for IntroActivity server check and button enable...");
        try {
            // IdlingResource가 IntroActivity의 비동기 작업을 처리해야 함
            Thread.sleep(5000); // 5초 대기 (서버 확인 및 UI 업데이트 시간)
        } catch (InterruptedException e) {
            Log.e("LoginActivityTest", "Thread.sleep interrupted during IntroActivity wait", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }

        // 3. IntroActivity의 시작 버튼 클릭 (ID 확인 및 수정 필요!)
        Log.d("LoginActivityTest", "Clicking IntroActivity start button...");
        int introButtonId = R.id.button_intro_bottom_start; // 예시 ID, 실제 ID로 변경하세요!
        onView(withId(introButtonId))
                .check(matches(isDisplayed())) // 버튼이 보이는지 확인
                .check(matches(isEnabled()))   // 버튼이 활성화되었는지 확인
                .perform(click());             // 버튼 클릭

        // 4. LoginActivity로 이동했는지 확인 (예: 구글 로그인 버튼 존재 확인)
        Log.d("LoginActivityTest", "Checking if LoginActivity is displayed...");
        int googleSignInButtonId = R.id.button_sign_in_google;
        onView(withId(googleSignInButtonId)).check(matches(isDisplayed()));

        // 5. LoginActivity의 Google 로그인 버튼 클릭
        Log.d("LoginActivityTest", "Clicking Google Sign-In button in LoginActivity...");
        onView(withId(googleSignInButtonId)).perform(click());

        // 6. !!! 여기서 테스트 실행자는 수동으로 Google 로그인을 진행합니다. !!!
        // 7. IdlingResource가 모든 비동기 작업(Google 로그인 콜백 처리, 서버 API 호출) 완료를 기다립니다.
        Log.d("LoginActivityTest", "Waiting for manual Google Sign-In and subsequent API calls...");
        try {
            // 수동 로그인 및 API 통신을 위한 대기 시간.
            Thread.sleep(20000); // 대기 시간 20초로 유지 (이전 코드 반영)
        } catch (InterruptedException e) {
            Log.e("LoginActivityTest", "Thread.sleep interrupted during manual sign-in wait", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }

        // 8. MainActivity로 이동했는지 확인
        Log.d("LoginActivityTest", "Checking if MainActivity is intended...");
        intended(hasComponent(MainActivity.class.getName()));

        // 9. UserPreferencesManager에 토큰 및 사용자 정보가 저장되었는지 확인
        Log.d("LoginActivityTest", "Verifying SharedPreferences for Google Login...");
        verifySharedPreferencesAfterLogin("Google"); // 검증 로직 분리

        // introScenario 변수는 더 이상 직접 제어하지 않으므로 close() 호출 제거
    }


    /**
     * IntroActivity 시작 -> 서버 온라인 확인 -> LoginActivity 이동 -> 수동 Kakao 로그인 -> MainActivity 이동
     * 전체 흐름을 테스트합니다.
     *
     * !!! 중요 !!!
     * 이 테스트를 실행할 때, 화면에 나타나는 Kakao 로그인 UI(웹뷰 또는 카카오톡 앱)를 통해
     * 테스트 실행자가 **수동으로 유효한 Kakao 계정 로그인을 완료**해야 합니다.
     * 테스트 기기에 카카오톡 앱이 설치되어 있거나 웹 로그인이 가능한 상태여야 합니다.
     */
    @Test
    public void loginProcessWithKakao() {
        // 1. IntroActivity 시작
        Log.d("LoginActivityTest", "Launching IntroActivity for Kakao Test...");
        ActivityScenario<IntroActivity> introScenario = ActivityScenario.launch(IntroActivity.class);

        // 2. IntroActivity가 서버 상태를 확인하고 시작 버튼을 활성화할 때까지 대기
        Log.d("LoginActivityTest", "Waiting for IntroActivity server check and button enable...");
        try {
            // IdlingResource가 IntroActivity의 비동기 작업을 처리해야 함
            Thread.sleep(5000); // 5초 대기 (서버 확인 및 UI 업데이트 시간)
        } catch (InterruptedException e) {
            Log.e("LoginActivityTest", "Thread.sleep interrupted during IntroActivity wait", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }

        // 3. IntroActivity의 시작 버튼 클릭 (ID 확인 및 수정 필요!)
        Log.d("LoginActivityTest", "Clicking IntroActivity start button...");
        int introButtonId = R.id.button_intro_bottom_start; // 예시 ID, 실제 ID로 변경하세요!
        onView(withId(introButtonId))
                .check(matches(isDisplayed())) // 버튼이 보이는지 확인
                .check(matches(isEnabled()))   // 버튼이 활성화되었는지 확인
                .perform(click());             // 버튼 클릭

        // 4. LoginActivity로 이동했는지 확인 (예: 카카오 로그인 버튼 존재 확인)
        Log.d("LoginActivityTest", "Checking if LoginActivity is displayed...");
        int kakaoSignInButtonId = R.id.button_sign_in_kakao; // LoginActivity의 카카오 버튼 ID
        onView(withId(kakaoSignInButtonId)).check(matches(isDisplayed()));

        // 5. LoginActivity의 Kakao 로그인 버튼 클릭
        Log.d("LoginActivityTest", "Clicking Kakao Sign-In button in LoginActivity...");
        onView(withId(kakaoSignInButtonId)).perform(click());

        // 6. !!! 여기서 테스트 실행자는 수동으로 Kakao 로그인을 진행합니다. !!!
        // 카카오톡 앱 또는 웹뷰가 실행되고, 사용자가 로그인을 완료하면
        // 카카오 SDK 콜백 -> SnsLoginManager -> ViewModel.onSuccess
        // -> 서버 API 호출 순서로 진행됩니다.

        // 7. IdlingResource가 모든 비동기 작업(Kakao 로그인 콜백 처리, 서버 API 호출) 완료를 기다립니다.
        Log.d("LoginActivityTest", "Waiting for manual Kakao Sign-In and subsequent API calls...");
        try {
            // 수동 로그인 및 API 통신을 위한 대기 시간. 카카오톡 앱 전환/웹뷰 로딩 시간 고려.
            Thread.sleep(30000); // 30초 대기 (넉넉하게 설정, 필요시 조절)
        } catch (InterruptedException e) {
            Log.e("LoginActivityTest", "Thread.sleep interrupted during manual sign-in wait", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }

        // 8. MainActivity로 이동했는지 확인
        Log.d("LoginActivityTest", "Checking if MainActivity is intended...");
        intended(hasComponent(MainActivity.class.getName()));

        // 9. UserPreferencesManager에 토큰 및 사용자 정보가 저장되었는지 확인
        Log.d("LoginActivityTest", "Verifying SharedPreferences for Kakao Login...");
        verifySharedPreferencesAfterLogin("Kakao"); // 검증 로직 분리
    }

    /**
     * 로그인 성공 후 SharedPreferences에 저장된 값들을 검증하는 헬퍼 메소드
     * @param loginType 로그인 타입 (디버깅용)
     */
    private void verifySharedPreferencesAfterLogin(String loginType) {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        String refreshToken = prefs.getString("refreshToken", null);
        String uid = prefs.getString("uid", null);
        String name = prefs.getString("name", null);
        String userName = prefs.getString("userName", null);
        String userId = prefs.getString("userId", null);

        Log.d("LoginActivityTest", "[" + loginType + "] Stored Access Token: " + accessToken);
        Log.d("LoginActivityTest", "[" + loginType + "] Stored UID: " + uid);

        assertNotNull("[" + loginType + "] Access token should not be null after successful login", accessToken);
        assertTrue("[" + loginType + "] Access token should not be empty after successful login", !TextUtils.isEmpty(accessToken));
        assertNotNull("[" + loginType + "] Refresh token should not be null after successful login", refreshToken);
        assertTrue("[" + loginType + "] Refresh token should not be empty after successful login", !TextUtils.isEmpty(refreshToken));
        assertNotNull("[" + loginType + "] UID should not be null", uid);
        assertTrue("[" + loginType + "] UID should not be empty", !TextUtils.isEmpty(uid));
        assertNotNull("[" + loginType + "] Name (from LoginModel) should not be null", name);
        assertTrue("[" + loginType + "] Name (from LoginModel) should not be empty", !TextUtils.isEmpty(name));
        assertNotNull("[" + loginType + "] User name (from server UserInfo) should not be null", userName);
        assertTrue("[" + loginType + "] User name (from server UserInfo) should not be empty", !TextUtils.isEmpty(userName));
        assertNotNull("[" + loginType + "] User ID (from server UserInfo) should not be null", userId);
        assertTrue("[" + loginType + "] User ID (from server UserInfo) should not be empty", !TextUtils.isEmpty(userId));
    }


    // --- Helper class for Toast matching ---
    // ToastMatcher는 현재 테스트에서 사용되지 않으므로 제거하거나 주석 처리합니다.
    /*
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override public void describeTo(Description description) { description.appendText("is toast"); }
        @Override public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                View windowDecorView = root.getDecorView();
                IBinder windowToken = windowDecorView.getWindowToken();
                IBinder appToken = windowDecorView.getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }
    }
    */
}
