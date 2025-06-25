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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
// import static org.hamcrest.CoreMatchers.not; // 필요시 사용
// import static org.hamcrest.Matchers.allOf;   // 필요시 사용
import static org.junit.Assert.assertNotNull; // assertNotNull은 verifySharedPreferencesAfterLogin에서 사용
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
// import team.y2k2.globa.main.search.SearchActivity; // 현재 테스트에서 직접 사용 안 함
// import team.y2k2.globa.notification.NotificationActivity; // 현재 테스트에서 직접 사용 안 함

@RunWith(AndroidJUnit4.class)
public class StatisticsFragmentTest {

    private static final String TAG = "StatisticsFragmentTest";

    // MainActivity UI 요소 ID
    private static final int mainActivityStatisticsTabId = R.id.item_main_statistics;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity 홈 화면 확인용


    // StatisticsFragment UI 요소 ID
    private static final int statisticsMainTitleId = R.id.textview_visualization;
    private static final int statisticsWordChartTitleId = R.id.textview_statistics_title_word;
    private static final int statisticsWordBarChartId = R.id.wordBarChart;
    private static final int statisticsTimeChartTitleId = R.id.textview_statistics_title_time;
    private static final int statisticsTimeLineChartId = R.id.timeLineChart;
    private static final int statisticsScoreChartTitleId = R.id.textview_statistics_title_score;
    private static final int statisticsGradeLineChartId = R.id.gradeLineChart;

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

    @Test
    public void testStatisticsFragment_UI_Display_AfterFullFlow() {
        Log.d(TAG, "testStatisticsFragment_UI_Display_AfterFullFlow: 테스트 시작");

        // === 1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 ===
        Log.d(TAG, "  1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 시작");
        try {
            boolean onMainActivityAlready = false;
            try {
                Thread.sleep(2000);
                onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
                Log.d(TAG, "    MainActivity가 이미 표시됨. 로그인 플로우 건너뜀.");
                onMainActivityAlready = true;
            } catch (NoMatchingViewException e) {
                Log.d(TAG, "    MainActivity가 즉시 표시되지 않음. 인트로/로그인 플로우 진행.");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                fail("Interrupted while checking for initial MainActivity: " + ie.getMessage());
                return;
            }

            if (!onMainActivityAlready) {
                Thread.sleep(3000); // IntroActivity UI 안정화 (위의 2초 + 3초 = 총 5초 대기)
                Log.d(TAG, "    인트로 화면 시작 버튼 클릭");
                onView(withId(R.id.button_intro_bottom_start)).perform(click());

                Thread.sleep(1000);
                Log.d(TAG, "    로그인 화면 구글 로그인 버튼 클릭");
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
            fail("로그인 또는 MainActivity 진입 중단됨: " + e.getMessage());
            return;
        } catch (Exception e) {
            Log.e(TAG, "로그인 또는 MainActivity 진입 중 예외 발생", e);
            fail("로그인 또는 MainActivity 진입 중 예외 발생: " + e.getMessage());
            return;
        }
        Log.d(TAG, "  1단계: 로그인 플로우 및 MainActivity 진입 완료 (또는 이미 진입됨)");


        // === 2단계: MainActivity에서 통계 탭으로 이동 ===
        Log.d(TAG, "  2단계: 통계 탭으로 이동 시작");
        try {
            Thread.sleep(1000);
            onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
            onView(withId(mainActivityStatisticsTabId)).perform(click());
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (통계 탭 이동 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        Log.d(TAG, "  2단계: 통계 탭으로 이동 완료");

        // === 3단계: StatisticsFragment 데이터 로딩 및 차트 렌더링 대기 ===
        Log.d(TAG, "  3단계: 통계 데이터 로딩 및 차트 렌더링 대기 중...");
        try {
            Thread.sleep(7000); // IdlingResource로 대체 권장
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        Log.d(TAG, "  3단계: 통계 데이터 로딩 및 차트 렌더링 대기 완료 (가정)");

        // === 4단계: StatisticsFragment UI 요소 검증 ===
        Log.d(TAG, "  4단계: StatisticsFragment UI 요소 검증 시작");
        onView(withId(statisticsMainTitleId)).check(matches(isDisplayed()));

        onView(withId(statisticsWordChartTitleId)).check(matches(isDisplayed()));
        onView(withId(statisticsWordChartTitleId)).check(matches(withText(R.string.fragment_statistics_title_word)));

        onView(withId(statisticsTimeChartTitleId)).check(matches(isDisplayed()));
        onView(withId(statisticsTimeChartTitleId)).check(matches(withText(R.string.activity_docs_statistics_title_time))); // 통계 프래그먼트용 문자열 리소스 사용 권장

        onView(withId(statisticsScoreChartTitleId)).check(matches(isDisplayed()));
        onView(withId(statisticsScoreChartTitleId)).check(matches(withText(R.string.activity_docs_statistics_title_score))); // 통계 프래그먼트용 문자열 리소스 사용 권장

        onView(withId(statisticsWordBarChartId)).check(matches(isDisplayed()));
        Log.d(TAG, "    단어 중요도 차트(wordBarChart) 표시됨.");

        onView(withId(statisticsTimeLineChartId)).check(matches(isDisplayed()));
        Log.d(TAG, "    학습 시간 차트(timeLineChart) 표시됨.");

        onView(withId(statisticsGradeLineChartId)).check(matches(isDisplayed()));
        Log.d(TAG, "    퀴즈 점수 차트(gradeLineChart) 표시됨.");

        Log.d(TAG, "  4단계: StatisticsFragment UI 요소 검증 완료.");
        Log.d(TAG, "testStatisticsFragment_UI_Display_AfterFullFlow: 모든 테스트 완료");
    }

    private void verifySharedPreferencesAfterLogin(String loginType) {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        String refreshToken = prefs.getString("refreshToken", null); // 추가된 필드
        String uid = prefs.getString("uid", null);
        String name = prefs.getString("name", null);
        String userName = prefs.getString("userName", null);
        String userId = prefs.getString("userId", null);

        Log.d(TAG, "    [" + loginType + "] 저장된 Access Token: " + accessToken);
        Log.d(TAG, "    [" + loginType + "] 저장된 UID (SNS): " + uid);

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