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
import androidx.test.espresso.matcher.ViewMatchers; // 명시적 import
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

/**
 * IntroActivity에서 시작하는 통합 UI 테스트 클래스입니다.
 * IntroActivity의 서버 상태 확인 후 LoginActivity로 이동하여,
 * 실제 Google 로그인을 수행하고 MainActivity로 전환되는 전체 흐름을 테스트합니다.
 */
@RunWith(AndroidJUnit4.class)
public class IntroActivityTest {

    // originalBaseUrl 변수 제거

    /**
     * 각 테스트 메소드 실행 전에 호출됩니다.
     * Espresso Intents를 초기화하고 EspressoIdlingResource를 등록합니다.
     * 주의: SharedPreferences 초기화 로직은 제거되었습니다.
     * 테스트 실행 전 앱 데이터가 초기 상태인지 확인해야 할 수 있습니다.
     */
    @Before
    public void setUp() {
        Intents.init(); // Espresso Intents 초기화
        // UserPreferencesManager.clearPreferences(...) 제거
        // ApiClient 초기화는 IntroActivity 내부에서 수행될 것으로 가정
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * 각 테스트 메소드 실행 후에 호출됩니다.
     * Espresso Intents를 해제하고 EspressoIdlingResource를 해제합니다.
     * 주의: SharedPreferences 초기화 로직은 제거되었습니다.
     */
    @After
    public void tearDown() {
        Intents.release(); // Espresso Intents 해제
        // UserPreferencesManager.clearPreferences(...) 제거
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * IntroActivity가 실행되었을 때, 앱 로고 이미지 뷰(R.id.imageview_intro_logo)가
     * 화면에 정상적으로 표시되는지 확인하는 테스트입니다.
     */
    @Test
    public void introActivity_displaysLogo() {
        ActivityScenario<IntroActivity> scenario = ActivityScenario.launch(IntroActivity.class);

        onView(ViewMatchers.withId(R.id.imageview_intro_logo))
                .check(matches(isDisplayed()));

        scenario.close(); // 시나리오 종료
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
    public void intro_serverOnline_manualGoogleSignIn_navigateToMain() {
        // 1. IntroActivity 시작
        ActivityScenario<IntroActivity> introScenario = ActivityScenario.launch(IntroActivity.class);

        // 2. IntroActivity가 서버 상태를 확인하고 시작 버튼을 활성화할 때까지 대기
        // IdlingResource가 IntroActivity의 isServerOpened() 비동기 호출을 처리해야 합니다.
        // 또는 충분한 시간 대기가 필요합니다.
        Log.d("IntroActivityTest", "Waiting for IntroActivity server check...");
        try {
            // 실제 서버 응답 및 IntroActivity 내부 로직 처리 시간 고려
            Thread.sleep(3000); // 3초 대기 (IdlingResource 구현에 따라 조절/제거 가능)
        } catch (InterruptedException e) {
            Log.e("IntroActivityTest", "Thread.sleep interrupted", e);
            Thread.currentThread().interrupt();
        }

        // 3. IntroActivity의 시작 버튼 클릭 (ID: button_intro_bottom_start 확인 필요)
        onView(withId(R.id.button_intro_bottom_start)) // 실제 IntroActivity 레이아웃의 버튼 ID로 변경
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        // 4. LoginActivity로 이동했는지 확인 (예: 로그인 버튼 존재 확인)
        // LoginActivity의 UI 요소가 나타날 때까지 잠시 대기할 수 있습니다.
        Log.d("IntroActivityTest", "Checking if LoginActivity is displayed...");
        onView(withId(R.id.button_sign_in_google)).check(matches(isDisplayed()));

        // 5. LoginActivity의 Google 로그인 버튼 클릭
        Log.d("IntroActivityTest", "Clicking Google Sign-In button...");
        onView(withId(R.id.button_sign_in_google)).perform(click());

        // 6. !!! 여기서 테스트 실행자는 수동으로 Google 로그인을 진행합니다. !!!
        // Google 로그인 UI가 나타나고, 사용자가 로그인을 완료하면
        // LoginActivity의 onActivityResult -> SnsLoginManager -> LoginViewModel.onSuccess
        // -> 서버 API 호출 (requestSignIn, requestUserInfo) 순서로 진행됩니다.

        // 7. IdlingResource가 모든 비동기 작업(Google 로그인 콜백 처리, 서버 API 호출) 완료를 기다립니다.
        // 수동 로그인 및 API 통신을 위한 충분한 시간 대기가 필요합니다.
        Log.d("IntroActivityTest", "Waiting for manual Google Sign-In and subsequent API calls...");
        try {
            // 수동 로그인 및 API 통신을 위한 대기 시간.
            Thread.sleep(45000); // 45초 대기 (넉넉하게 설정, 필요시 조절)
        } catch (InterruptedException e) {
            Log.e("IntroActivityTest", "Thread.sleep interrupted", e);
            Thread.currentThread().interrupt();
        }

        // 8. MainActivity로 이동했는지 확인
        Log.d("IntroActivityTest", "Checking if MainActivity is intended...");
        intended(hasComponent(MainActivity.class.getName()));

        // 9. UserPreferencesManager에 토큰 및 사용자 정보가 저장되었는지 확인
        Log.d("IntroActivityTest", "Verifying SharedPreferences...");
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        String refreshToken = prefs.getString("refreshToken", null);
        String uid = prefs.getString("uid", null); // Google UID (또는 서버에서 할당한 ID)
        String name = prefs.getString("name", null); // Google 계정 이름 (또는 서버에서 받은 이름)
        String userName = prefs.getString("userName", null); // 서버에서 받은 사용자 이름
        String userId = prefs.getString("userId", null); // 서버에서 받은 사용자 ID

        Log.d("IntroActivityTest", "Stored Access Token: " + accessToken);
        Log.d("IntroActivityTest", "Stored UID: " + uid);

        assertNotNull("Access token should not be null after successful login", accessToken);
        assertTrue("Access token should not be empty after successful login", !TextUtils.isEmpty(accessToken));
        assertNotNull("Refresh token should not be null after successful login", refreshToken);
        assertTrue("Refresh token should not be empty after successful login", !TextUtils.isEmpty(refreshToken));
        assertNotNull("UID should not be null", uid);
        assertTrue("UID should not be empty", !TextUtils.isEmpty(uid));
        assertNotNull("Name (from LoginModel) should not be null", name);
        assertTrue("Name (from LoginModel) should not be empty", !TextUtils.isEmpty(name));
        assertNotNull("User name (from server UserInfo) should not be null", userName);
        assertTrue("User name (from server UserInfo) should not be empty", !TextUtils.isEmpty(userName));
        assertNotNull("User ID (from server UserInfo) should not be null", userId);
        assertTrue("User ID (from server UserInfo) should not be empty", !TextUtils.isEmpty(userId));

        // introScenario는 더 이상 사용하지 않으므로 close() 호출은 불필요합니다.
        // ActivityScenario는 자동으로 관리됩니다.
    }

    // 서버 오프라인 테스트 제거
    // @Test
    // public void introActivity_serverOffline_showsDialogAndFinishesOnConfirm() { ... }

    // --- Helper class for Toast matching ---
    // ToastMatcher는 LoginActivityTest에서 필요시 사용될 수 있으므로 여기서는 제거합니다.
    // 만약 IntroActivity에서도 Toast를 사용한다면 여기에 유지하거나 별도 유틸 클래스로 분리합니다.
    /*
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        // ... 구현 ...
    }
    */
}
