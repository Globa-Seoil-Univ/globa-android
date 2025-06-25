package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
// import static androidx.test.espresso.Espresso.pressBack; // 필요시 사용
import static androidx.test.espresso.action.ViewActions.click;
// import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard; // 필요시 사용
// import static androidx.test.espresso.action.ViewActions.replaceText;    // 필요시 사용
import static androidx.test.espresso.assertion.ViewAssertions.matches;
// import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition; // 현재 테스트에서 미사용
// import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder; // 현재 테스트에서 미사용
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
// import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant; // 현재 테스트에서 미사용
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
// import static androidx.test.espresso.matcher.ViewMatchers.withText;  // 필요시 사용
// import static org.hamcrest.CoreMatchers.not; // 필요시 사용
// import static org.hamcrest.Matchers.allOf;   // 필요시 사용
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
// import android.view.View; // View 직접 참조 안 함

// import androidx.annotation.NonNull; // BoundedMatcher 사용 안 하므로 제거 가능
// import androidx.recyclerview.widget.RecyclerView; // RecyclerView 직접 참조 안 함
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
// import androidx.test.espresso.matcher.BoundedMatcher; // BoundedMatcher 사용 안 하므로 제거 가능
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

// import org.hamcrest.Description; // BoundedMatcher 사용 안 하므로 제거 가능
// import org.hamcrest.Matcher;     // BoundedMatcher 사용 안 하므로 제거 가능
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import team.y2k2.globa.EspressoIdlingResource;
import team.y2k2.globa.R;
// import team.y2k2.globa.docs.DocsActivity; // 현재 테스트에서 직접 사용 안 함
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private static final String TAG = "LoginActivityTest";

    // MainActivity의 Fragment 컨테이너 ID 및 홈 화면(MainFragment) 활성화 확인용 View ID
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // 실제 MainActivity의 홈 화면을 나타내는 View ID

    @Rule
    public ActivityScenarioRule<IntroActivity> activityRule = new ActivityScenarioRule<>(IntroActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        Log.d(TAG, "setUp: 테스트 준비 완료 (Intents 초기화, IdlingResource 등록)");
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        Intents.release();
        Log.d(TAG, "tearDown: 테스트 환경 정리 완료 (Intents 해제, IdlingResource 해제)");
    }

    private boolean checkIfAlreadyOnMainActivity() throws InterruptedException {
        try {
            // IntroActivity가 빠르게 MainActivity로 리다이렉트하는 경우를 위해 짧게 대기
            Thread.sleep(3000); // 앱 초기화 및 자동 로그인/리다이렉션 시간 부여 (IdlingResource로 대체 권장)
            onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
            Log.d(TAG, "    MainActivity가 이미 표시됨. 로그인 플로우 건너뜀.");
            return true;
        } catch (NoMatchingViewException e) {
            Log.d(TAG, "    MainActivity가 즉시 표시되지 않음. 인트로/로그인 플로우 진행 필요.");
            return false;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail("Interrupted while checking for initial MainActivity: " + ie.getMessage());
            return false; // 테스트 실패 처리
        }
    }

    @Test
    public void loginProcessWithGoogle() {
        Log.d(TAG, "loginProcessWithGoogle: 테스트 시작");
        try {
            boolean onMainActivityAlready = checkIfAlreadyOnMainActivity();

            if (!onMainActivityAlready) {
                // IntroActivity는 ActivityScenarioRule에 의해 이미 실행된 상태
                // IntroActivity의 서버 상태 확인 및 UI 안정화 대기 (위의 3초 + 추가 2초 = 총 5초)
                Thread.sleep(2000);
                Log.d(TAG, "    인트로 화면 시작 버튼 클릭");
                onView(withId(R.id.button_intro_bottom_start))
                        .check(matches(isDisplayed()))
                        .check(matches(isEnabled()))
                        .perform(click());

                Thread.sleep(1000); // LoginActivity 전환 대기
                Log.d(TAG, "    로그인 화면 구글 로그인 버튼 클릭");
                onView(withId(R.id.button_sign_in_google)).check(matches(isDisplayed()));
                onView(withId(R.id.button_sign_in_google)).perform(click());

                Log.d(TAG, "    수동 Google 로그인 대기 중... (20초)");
                Thread.sleep(20000);

                Log.d(TAG, "    MainActivity로 전환되었는지 확인");
                intended(hasComponent(MainActivity.class.getName()));
            }

            onView(withId(mainActivityHomeIndicatorId)).check(matches(isDisplayed()));
            verifySharedPreferencesAfterLogin("Google");

        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (로그인 플로우 중)", e);
            Thread.currentThread().interrupt();
            fail("Google 로그인 플로우 중단됨: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Google 로그인 플로우 중 예외 발생", e);
            fail("Google 로그인 플로우 중 예외 발생: " + e.getMessage());
        }
        Log.d(TAG, "loginProcessWithGoogle: 테스트 완료 (또는 이미 로그인됨)");
    }

    @Test
    public void loginProcessWithKakao() {
        Log.d(TAG, "loginProcessWithKakao: 테스트 시작");
        try {
            boolean onMainActivityAlready = checkIfAlreadyOnMainActivity();

            if (!onMainActivityAlready) {
                Thread.sleep(2000); // IntroActivity UI 안정화
                Log.d(TAG, "    인트로 화면 시작 버튼 클릭");
                onView(withId(R.id.button_intro_bottom_start))
                        .check(matches(isDisplayed()))
                        .check(matches(isEnabled()))
                        .perform(click());

                Thread.sleep(1000); // LoginActivity 전환 대기
                Log.d(TAG, "    로그인 화면 카카오 로그인 버튼 클릭");
                onView(withId(R.id.button_sign_in_kakao)).check(matches(isDisplayed()));
                onView(withId(R.id.button_sign_in_kakao)).perform(click());

                Log.d(TAG, "    수동 Kakao 로그인 대기 중... (30초)");
                Thread.sleep(30000);

                Log.d(TAG, "    MainActivity로 전환되었는지 확인");
                intended(hasComponent(MainActivity.class.getName()));
            }

            onView(withId(mainActivityHomeIndicatorId)).check(matches(isDisplayed()));
            verifySharedPreferencesAfterLogin("Kakao");

        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (로그인 플로우 중)", e);
            Thread.currentThread().interrupt();
            fail("Kakao 로그인 플로우 중단됨: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Kakao 로그인 플로우 중 예외 발생", e);
            fail("Kakao 로그인 플로우 중 예외 발생: " + e.getMessage());
        }
        Log.d(TAG, "loginProcessWithKakao: 테스트 완료 (또는 이미 로그인됨)");
    }

    private void verifySharedPreferencesAfterLogin(String loginType) {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        String refreshToken = prefs.getString("refreshToken", null);
        String uid = prefs.getString("uid", null);
        String name = prefs.getString("name", null);
        String userName = prefs.getString("userName", null);
        String userId = prefs.getString("userId", null);

        Log.d(TAG, "    [" + loginType + "] 저장된 Access Token: " + accessToken);
        Log.d(TAG, "    [" + loginType + "] 저장된 UID: " + uid);

        assertNotNull("[" + loginType + "] 로그인 성공 후 Access token은 null이 아니어야 합니다.", accessToken);
        assertTrue("[" + loginType + "] 로그인 성공 후 Access token은 비어있지 않아야 합니다.", !TextUtils.isEmpty(accessToken));
        assertNotNull("[" + loginType + "] 로그인 성공 후 Refresh token은 null이 아니어야 합니다.", refreshToken);
        assertTrue("[" + loginType + "] 로그인 성공 후 Refresh token은 비어있지 않아야 합니다.", !TextUtils.isEmpty(refreshToken));
        assertNotNull("[" + loginType + "] UID는 null이 아니어야 합니다.", uid);
        assertTrue("[" + loginType + "] UID는 비어있지 않아야 합니다.", !TextUtils.isEmpty(uid));
        assertNotNull("[" + loginType + "] 이름(SNS 로그인 시)은 null이 아니어야 합니다.", name);
        assertTrue("[" + loginType + "] 이름(SNS 로그인 시)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(name));
        assertNotNull("[" + loginType + "] 사용자 이름(서버 UserInfo)은 null이 아니어야 합니다.", userName);
        assertTrue("[" + loginType + "] 사용자 이름(서버 UserInfo)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(userName));
        assertNotNull("[" + loginType + "] 사용자 ID(서버 UserInfo)는 null이 아니어야 합니다.", userId);
        assertTrue("[" + loginType + "] 사용자 ID(서버 UserInfo)는 비어있지 않아야 합니다.", !TextUtils.isEmpty(userId));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }
}