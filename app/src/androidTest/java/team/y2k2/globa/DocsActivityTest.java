package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
// import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard; // DocsActivityTest에서는 현재 미사용
// import static androidx.test.espresso.action.ViewActions.replaceText; // DocsActivityTest에서는 현재 미사용
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
// import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder; // DocsActivityTest에서는 현재 미사용
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
// import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra; // DocsActivityTest에서는 현재 미사용
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
// import static org.hamcrest.CoreMatchers.allOf; // DocsActivityTest에서는 현재 미사용
// import static org.hamcrest.Matchers.emptyOrNullString; // DocsActivityTest에서는 현재 미사용
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail; // fail 추가

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

// import androidx.recyclerview.widget.RecyclerView; // 직접적인 타입 참조 없으면 제거 가능
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException; // NoMatchingViewException import
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

import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DocsActivityTest {

    private static final String TAG = "DocsActivityTest";

    // DocsActivity UI 요소 ID
    private static final int docsTitleId = R.id.textview_docs_title;
    private static final int backButtonId = R.id.image_button_docs_back;
    private static final int moreButtonId = R.id.imageview_docs_more;
    private static final int descriptionButtonId = R.id.button_docs_description;
    private static final int summaryButtonId = R.id.button_docs_summary;
    private static final int docsRecyclerViewId = R.id.recyclerview_docs_detail;

    // MainFragment UI 요소 ID (문서 목록 접근용)
    private static final int mainFragmentDocsRecyclerViewId = R.id.recyclerview_main_document; // 실제 ID로 변경 필요
    // MainActivity의 홈 화면(MainFragment)을 나타내는 View ID
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;


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
    public void testFullFlow_MainToDocs_AndVerifyInteractions() {
        Log.d(TAG, "testFullFlow_MainToDocs_AndVerifyInteractions: 테스트 시작");

        // === 1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 ===
        Log.d(TAG, "  1단계: 로그인 플로우 또는 MainActivity 직접 실행 확인 시작");
        try {
            boolean onMainActivityAlready = false;
            try {
                // IntroActivity가 빠르게 MainActivity로 리다이렉트하는 경우를 위해 짧게 대기
                Thread.sleep(2000); // 앱 초기화 및 자동 로그인/리다이렉션 시간 부여
                onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
                // 위 라인에서 예외가 발생하지 않으면 MainActivity가 이미 표시된 것임
                Log.d(TAG, "    MainActivity가 이미 표시됨. 로그인 플로우 건너뜀.");
                onMainActivityAlready = true;
            } catch (NoMatchingViewException e) {
                // MainActivity가 즉시 보이지 않으면, IntroActivity부터 시작하는 전체 로그인 플로우 진행
                Log.d(TAG, "    MainActivity가 즉시 표시되지 않음. 인트로/로그인 플로우 진행.");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                fail("Interrupted while checking for initial MainActivity: " + ie.getMessage());
                return;
            }

            if (!onMainActivityAlready) {
                // IntroActivity는 ActivityScenarioRule에 의해 이미 실행된 상태
                // IntroActivity의 UI 요소가 완전히 로드될 시간을 추가로 부여
                Thread.sleep(3000); // 이전 5000에서 2000을 위에서 사용했으므로 나머지
                Log.d(TAG, "    인트로 화면 시작 버튼 클릭");
                onView(withId(R.id.button_intro_bottom_start)).perform(click());

                Thread.sleep(1000); // LoginActivity 전환 대기
                Log.d(TAG, "    로그인 화면 구글 로그인 버튼 클릭");
                onView(withId(R.id.button_sign_in_google)).perform(click());

                Log.d(TAG, "    수동 Google 로그인 대기 중... (20초)");
                Thread.sleep(20000); // 수동 로그인 및 API 통신, MainActivity로의 전환 대기

                Log.d(TAG, "    MainActivity로 전환되었는지 확인");
                intended(hasComponent(MainActivity.class.getName()));
            }

            // MainActivity에 도달했는지 최종 확인 및 기본 상태(예: 홈 탭) 확인
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
            Thread.sleep(3000); // 문서 목록 API 로딩 및 UI 반영 대기
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


        Log.d(TAG, "    DocsActivity로 전환되었는지 확인");
        // DocsActivity로의 전환은 여러번 발생할 수 있으므로, times()는 상황에 맞게 조절해야함.
        // 여기서는 이 테스트 메소드 내에서 DocsActivity가 처음 실행되는 것이므로 times(1)
        intended(hasComponent(DocsActivity.class.getName()), Intents.times(1));


        // === 3단계: DocsActivity 기능 테스트 ===
        Log.d(TAG, "  3단계: DocsActivity 기능 테스트 시작");
        Log.d(TAG, "    DocsActivity API 데이터 로딩 대기 중...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (DocsActivity API 데이터 로딩 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }

        // 3.1 DocsActivity 초기 UI 검증
        Log.d(TAG, "    3.1: DocsActivity 초기 UI 검증 시작");
        onView(withId(docsTitleId)).check(matches(isDisplayed())).check(matches(not(withText(""))));
        onView(withId(backButtonId)).check(matches(isDisplayed()));
        onView(withId(moreButtonId)).check(matches(isDisplayed()));
        onView(withId(descriptionButtonId)).check(matches(isDisplayed()));
        onView(withId(summaryButtonId)).check(matches(isDisplayed()));
        onView(withId(docsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    3.1: DocsActivity 초기 UI 검증 완료");

        // 3.2 본문/요약 탭 전환 테스트
        Log.d(TAG, "    3.2: 본문/요약 탭 전환 테스트 시작");
        onView(withId(summaryButtonId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(descriptionButtonId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    3.2: 본문/요약 탭 전환 테스트 완료");

        // 3.4 더보기 버튼 테스트
        Log.d(TAG, "    3.4: 더보기 버튼 테스트 시작");
        onView(withId(moreButtonId)).perform(click());
        intended(hasComponent(DocsMoreActivity.class.getName()), Intents.times(1)); // 이 테스트 내에서 DocsMoreActivity 첫 실행
        pressBack(); // DocsMoreActivity에서 DocsActivity로 돌아오기
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    3.4: 더보기 버튼 테스트 완료");

        // 3.5 뒤로가기 버튼 테스트
        Log.d(TAG, "    3.5: 뒤로가기 버튼 테스트 시작");
        onView(withId(backButtonId)).perform(click());
        // DocsActivity가 종료되고 MainActivity (MainFragment)로 돌아왔는지 확인
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withId(mainFragmentDocsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    3.5: 뒤로가기 버튼 테스트 완료 (MainActivity로 복귀 가정)");

        Log.d(TAG, "  3단계: DocsActivity 기능 테스트 완료");
        Log.d(TAG, "testFullFlow_MainToDocs_AndVerifyInteractions: 모든 테스트 완료");
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
        // 'name' 필드는 로그인 시점에 따라 다를 수 있으므로, null이 아닌지만 체크하거나 테스트 데이터에 따라 구체적으로 검증
        assertNotNull("[" + loginType + "] 이름(LoginModel에서)은 null이 아니어야 합니다.", name);
        // 'userName', 'userId'는 API 호출 후 저장되므로, 해당 시점에 검증
        assertNotNull("[" + loginType + "] 사용자 이름(서버 UserInfo에서)은 null이 아니어야 합니다.", userName);
        assertTrue("[" + loginType + "] 사용자 이름(서버 UserInfo에서)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(userName));
        assertNotNull("[" + loginType + "] 사용자 ID(서버 UserInfo에서)는 null이 아니어야 합니다.", userId);
        assertTrue("[" + loginType + "] 사용자 ID(서버 UserInfo에서)는 비어있지 않아야 합니다.", !TextUtils.isEmpty(userId));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }
}