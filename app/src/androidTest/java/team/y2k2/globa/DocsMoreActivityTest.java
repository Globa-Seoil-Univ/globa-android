package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
// import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder; // 현재 코드에서는 미사용
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
// import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant; // 현재 코드에서는 미사용
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
// import static org.hamcrest.Matchers.allOf; // 필요시 사용
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail; // fail 추가

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
// import android.widget.TextView; // isHolderWithTitle 사용 안 하므로 제거 가능

// import androidx.annotation.NonNull; // isHolderWithTitle 사용 안 하므로 제거 가능
// import androidx.recyclerview.widget.RecyclerView; // isHolderWithTitle 사용 안 하므로 제거 가능
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException; // NoMatchingViewException import
// import androidx.test.espresso.UiController; // ViewAction 사용 안 하므로 제거 가능
// import androidx.test.espresso.ViewAction; // ViewAction 사용 안 하므로 제거 가능
import androidx.test.espresso.intent.Intents;
// import androidx.test.espresso.matcher.BoundedMatcher; // isHolderWithTitle 사용 안 하므로 제거 가능
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

// import org.hamcrest.Description; // isHolderWithTitle 사용 안 하므로 제거 가능
// import org.hamcrest.Matcher; // isHolderWithTitle 사용 안 하므로 제거 가능
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import team.y2k2.globa.EspressoIdlingResource;
import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.edit.DocsNameEditActivity;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.docs.quiz.conduct.QuizActivity;
import team.y2k2.globa.docs.statistics.DocsStatisticsActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DocsMoreActivityTest {

    private static final String TAG = "DocsMoreActivityTest";

    // MainFragment UI 요소 ID
    private static final int mainFragmentDocsRecyclerViewId = R.id.recyclerview_main_document; // 실제 MainFragment의 문서 목록 RecyclerView ID
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main; // MainActivity의 FragmentContainerView ID
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity의 홈 화면을 나타내는 View ID

    // DocsActivity UI 요소 ID
    private static final int docsActivityMoreButtonId = R.id.imageview_docs_more;
    private static final int docsActivityDetailRecyclerViewId = R.id.recyclerview_docs_detail; // DocsActivity 내부 RecyclerView

    // DocsMoreActivity UI 요소 ID
    private static final int docsMoreBackButtonId = R.id.image_button_docs_more_back;
    private static final int docsMoreFolderTitleId = R.id.textview_docs_more_folder_title;
    private static final int docsMoreDocsTitleId = R.id.textview_docs_more_docs_title;
    private static final int renameLayoutId = R.id.relativelayout_docs_more_rename;
    private static final int deleteLayoutId = R.id.relativelayout_docs_more_delete;
    private static final int statisticsLayoutId = R.id.relativelayout_docs_more_statistics;
    private static final int quizLayoutId = R.id.relativelayout_docs_more_quiz;
    private static final int shareLayoutId = R.id.relativelayout_docs_more_share;

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
    public void testDocsMoreActivity_Interactions_AfterFullFlow() {
        Log.d(TAG, "testDocsMoreActivity_Interactions_AfterFullFlow: 테스트 시작");

        // === 1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 ===
        Log.d(TAG, "  1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 시작");
        try {
            boolean onMainActivityAlready = false;
            try {
                Thread.sleep(2000); // 앱 초기화 및 자동 로그인/리다이렉션 시간 부여
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


        // === 2단계: MainFragment에서 문서 선택하여 DocsActivity 실행 ===
        Log.d(TAG, "  2단계: MainFragment에서 문서 선택하여 DocsActivity 실행 시작");
        Log.d(TAG, "    MainFragment의 문서 목록 로딩 대기 중...");
        try {
            Thread.sleep(3000); // 실제 앱에서는 IdlingResource로 대체 필요
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (MainFragment 문서 목록 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        Log.d(TAG, "    MainFragment의 문서 목록(RecyclerView)에서 첫 번째 아이템 클릭");
        try {
            onView(withId(mainFragmentDocsRecyclerViewId))
                    .check(matches(isDisplayed()))
                    .perform(actionOnItemAtPosition(0, click()));
        } catch (Exception e) {
            Log.e(TAG, "MainFragment 문서 목록에서 아이템 클릭 실패", e);
            fail("MainFragment 문서 목록에서 아이템 클릭 실패: " + e.getMessage());
            return;
        }
        intended(hasComponent(DocsActivity.class.getName())); // DocsActivity가 1번 실행되는지 확인

        // === 3단계: DocsActivity에서 "더보기" 버튼 클릭하여 DocsMoreActivity 실행 ===
        Log.d(TAG, "  3단계: DocsActivity에서 '더보기' 클릭하여 DocsMoreActivity 실행 시작");
        try {
            Thread.sleep(3000); // DocsActivity 내용 로딩 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (DocsActivity 내용 로딩 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(docsActivityMoreButtonId)).perform(click());
        intended(hasComponent(DocsMoreActivity.class.getName())); // DocsMoreActivity가 1번 실행되는지 확인 (이 테스트 내에서)

        try {
            Thread.sleep(1000); // DocsMoreActivity 로드 대기
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }


        // === 4단계: DocsMoreActivity 기능 테스트 ===
        Log.d(TAG, "  4단계: DocsMoreActivity 기능 테스트 시작");

        // 4.1 초기 UI 검증
        Log.d(TAG, "    4.1: DocsMoreActivity 초기 UI 검증 시작");
        onView(withId(docsMoreFolderTitleId)).check(matches(isDisplayed())).check(matches(not(withText(""))));
        onView(withId(docsMoreDocsTitleId)).check(matches(isDisplayed())).check(matches(not(withText(""))));
        onView(withId(renameLayoutId)).check(matches(isDisplayed()));
        onView(withId(deleteLayoutId)).check(matches(isDisplayed()));
        onView(withId(statisticsLayoutId)).check(matches(isDisplayed()));
        onView(withId(quizLayoutId)).check(matches(isDisplayed()));
        onView(withId(shareLayoutId)).check(matches(isDisplayed()));
        Log.d(TAG, "    4.1: DocsMoreActivity 초기 UI 검증 완료");

        // 4.3 이름 변경 버튼 테스트
        Log.d(TAG, "    4.3: 이름 변경 버튼 테스트 시작");
        onView(withId(renameLayoutId)).perform(click());
        intended(hasComponent(DocsNameEditActivity.class.getName()));
        pressBack(); // DocsNameEditActivity에서 DocsMoreActivity로 돌아오기
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    4.3: 이름 변경 버튼 테스트 완료");

        // 4.4 시각화 자료 보기 버튼 테스트
        Log.d(TAG, "    4.4: 시각화 자료 보기 버튼 테스트 시작");
        onView(withId(statisticsLayoutId)).perform(click());
        intended(hasComponent(DocsStatisticsActivity.class.getName()));
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    4.4: 시각화 자료 보기 버튼 테스트 완료");

        // 4.5 퀴즈 풀기 버튼 테스트
        Log.d(TAG, "    4.5: 퀴즈 풀기 버튼 테스트 시작");
        onView(withId(quizLayoutId)).perform(click());
        intended(hasComponent(QuizActivity.class.getName()));
        pressBack(); // QuizActivity에서 DocsMoreActivity로 돌아오기
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    4.5: 퀴즈 풀기 버튼 테스트 완료");

        // 4.6 DocsMoreActivity 뒤로가기 버튼 테스트
        Log.d(TAG, "    4.6: DocsMoreActivity 뒤로가기 버튼 테스트 시작");
        onView(withId(docsMoreBackButtonId)).perform(click());
        // DocsActivity로 돌아왔는지 확인 (예: DocsActivity의 RecyclerView가 보이는지)
        onView(withId(docsActivityDetailRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    4.6: DocsMoreActivity 뒤로가기 버튼 테스트 완료, DocsActivity로 복귀 확인");

        Log.d(TAG, "  4단계: DocsMoreActivity 기능 테스트 완료");
        Log.d(TAG, "testDocsMoreActivity_Interactions_AfterFullFlow: 모든 테스트 완료");
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
        assertNotNull("[" + loginType + "] 이름(LoginModel에서)은 null이 아니어야 합니다.", name);
        assertTrue("[" + loginType + "] 이름(LoginModel에서)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(name));
        assertNotNull("[" + loginType + "] 사용자 이름(서버 UserInfo에서)은 null이 아니어야 합니다.", userName);
        assertTrue("[" + loginType + "] 사용자 이름(서버 UserInfo에서)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(userName));
        assertNotNull("[" + loginType + "] 사용자 ID(서버 UserInfo에서)는 null이 아니어야 합니다.", userId);
        assertTrue("[" + loginType + "] 사용자 ID(서버 UserInfo에서)는 비어있지 않아야 합니다.", !TextUtils.isEmpty(userId));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }
}