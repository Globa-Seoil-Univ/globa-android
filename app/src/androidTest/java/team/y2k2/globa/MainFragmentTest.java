package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
// import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard; // 필요시 사용
// import static androidx.test.espresso.action.ViewActions.replaceText;    // 필요시 사용
import static androidx.test.espresso.assertion.ViewAssertions.matches;
// import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition; // 현재 테스트에서 미사용
// import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder; // 현재 테스트에서 미사용
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
// import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant; // 필요시 사용
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
// import static androidx.test.espresso.matcher.ViewMatchers.withText;  // 필요시 사용
// import static org.hamcrest.CoreMatchers.not; // 필요시 사용
// import static org.hamcrest.Matchers.allOf;   // 필요시 사용
import static org.hamcrest.Matchers.not;
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
import team.y2k2.globa.main.search.SearchActivity;
import team.y2k2.globa.notification.NotificationActivity;

@RunWith(AndroidJUnit4.class)
public class MainFragmentTest { // 클래스 이름 MainFragmentTest로 유지

    private static final String TAG = "MainFragmentTest"; // TAG도 MainFragmentTest로 변경

    // MainActivity의 Fragment 컨테이너 ID 및 홈 화면(MainFragment) 활성화 확인용 View ID
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top;

    // MainFragment 내부 요소 ID
    private static final int titleId = R.id.textview_main_title;
    private static final int searchButtonId = R.id.image_button_main_search;
    private static final int notificationButtonId = R.id.image_button_main_notification;
    private static final int currentlyButtonId = R.id.button_main_docs_type_1;
    private static final int allButtonId = R.id.button_main_docs_type_2;
    private static final int shareButtonId = R.id.button_main_docs_type_3;
    private static final int sharedButtonId = R.id.button_main_docs_type_4;
    private static final int recyclerViewId = R.id.recyclerview_main_document;
    private static final int promotionViewPagerId = R.id.viewpager_main_carousel;
    private static final int swipeRefreshLayoutId = R.id.swiperefreshlayout_main;

    // MainActivity UI 요소 ID (하단 네비게이션 등)
    private static final int bottomNavId = R.id.bottom_navigation_main_bottom;
    private static final int mainTabId = R.id.item_main_main;
    private static final int folderTabId = R.id.item_main_folder;
    private static final int statisticsTabId = R.id.item_main_statistics;
    private static final int profileTabId = R.id.item_main_profile;

    // 각 Fragment의 루트 또는 주요 View ID (전환 확인용)
    private static final int mainFragmentViewId = R.id.fragment_main; // MainFragment의 루트 레이아웃 ID로 가정
    private static final int folderFragmentViewId = R.id.fragment_folder; // FolderFragment의 루트 레이아웃 ID로 가정
    private static final int statisticsFragmentViewId = R.id.fragment_statistics; // StatisticsFragment의 루트 레이아웃 ID로 가정
    private static final int profileFragmentViewId = R.id.recyclerview_profile_setting; // ProfileFragment의 RecyclerView ID로 가정

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
    public void testFullAppFlow_And_MainFragmentInteractions() {
        Log.d(TAG, "testFullAppFlow_And_MainFragmentInteractions: 테스트 시작");

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

                Thread.sleep(1000); // LoginActivity 전환 대기
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

        // === 2단계: MainActivity 및 MainFragment 테스트 시작 ===
        Log.d(TAG, "2단계: MainActivity 및 MainFragment 상호작용 테스트 시작");

        // 2.1. MainActivity 초기 상태 확인 (MainFragment가 기본으로 표시되는지)
        Log.d(TAG, "  2.1: MainActivity 초기 상태 (MainFragment 표시) 확인 중...");
        onView(withId(bottomNavId)).check(matches(isDisplayed()));
        onView(withId(mainTabId)).check(matches(isSelected()));
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed())); // MainFragment의 특정 뷰가 보이는지 확인
        onView(withId(titleId)).check(matches(isDisplayed()));

        // 2.2. MainFragment 상단 검색 버튼 테스트
        Log.d(TAG, "  2.2: MainFragment 검색 버튼 테스트 중...");
        onView(withId(searchButtonId)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(SearchActivity.class.getName()));
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  2.2: MainFragment 검색 버튼 테스트 완료");

        // 2.3. MainFragment 상단 알림 버튼 테스트
        Log.d(TAG, "  2.3: MainFragment 알림 버튼 테스트 중...");
        onView(withId(notificationButtonId)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(NotificationActivity.class.getName()));
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  2.3: MainFragment 알림 버튼 테스트 완료");

        // 2.4. MainFragment 카테고리 필터 버튼 테스트
        Log.d(TAG, "  2.4: MainFragment 카테고리 필터 버튼 테스트 중...");

        Log.d(TAG, "    2.4.1: '현재 작업 문서' 버튼 클릭 테스트 중...");
        onView(withId(currentlyButtonId)).check(matches(isDisplayed())).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // UI 변경 및 데이터 로드 대기
        Log.d(TAG, "    2.4.1: '현재 작업 문서' 버튼 테스트 완료");

        Log.d(TAG, "    2.4.2: '인기순/전체' 버튼 클릭 테스트 중...");
        onView(withId(allButtonId)).check(matches(isDisplayed())).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    2.4.2: '인기순/전체' 버튼 테스트 완료");

        Log.d(TAG, "    2.4.3: '공유한' 버튼 클릭 테스트 중...");
        onView(withId(shareButtonId)).check(matches(isDisplayed())).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    2.4.3: '공유한' 버튼 테스트 완료");

        Log.d(TAG, "    2.4.4: '공유받은' 버튼 클릭 테스트 중...");
        onView(withId(sharedButtonId)).check(matches(isDisplayed())).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Log.d(TAG, "    2.4.4: '공유받은' 버튼 테스트 완료");
        Log.d(TAG, "  2.4: MainFragment 카테고리 필터 버튼 테스트 완료");

        Log.d(TAG, "  MainFragment 내 RecyclerView 표시 확인 중...");
        onView(withId(recyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  MainFragment 내 프로모션 ViewPager 표시 확인 중...");
        onView(withId(promotionViewPagerId)).check(matches(isDisplayed()));
        Log.d(TAG, "  MainFragment 내 SwipeRefreshLayout 표시 확인 중...");
        onView(withId(swipeRefreshLayoutId)).check(matches(isDisplayed()));

        // === 하단 네비게이션 테스트 ===
        // 15. 폴더 탭 클릭 및 확인
        Log.d(TAG, "Clicking Folder tab...");
        onView(withId(folderTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // Fragment 전환 대기
        onView(withId(folderTabId)).check(matches(isSelected()));
        onView(withId(mainTabId)).check(matches(not(isSelected())));
        onView(withId(folderFragmentViewId)).check(matches(isDisplayed()));

        // 16. 통계 탭 클릭 및 확인
        Log.d(TAG, "Clicking Statistics tab...");
        onView(withId(statisticsTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(statisticsTabId)).check(matches(isSelected()));
        onView(withId(folderTabId)).check(matches(not(isSelected())));
        onView(withId(statisticsFragmentViewId)).check(matches(isDisplayed()));

        // 17. 프로필 탭 클릭 및 확인
        Log.d(TAG, "Clicking Profile tab...");
        onView(withId(profileTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileTabId)).check(matches(isSelected()));
        onView(withId(statisticsTabId)).check(matches(not(isSelected())));
        onView(withId(profileFragmentViewId)).check(matches(isDisplayed()));

        // 18. 다시 메인 탭 클릭 및 확인
        Log.d(TAG, "Clicking Main tab again...");
        onView(withId(mainTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(mainTabId)).check(matches(isSelected()));
        onView(withId(profileTabId)).check(matches(not(isSelected())));
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed()));

        Log.d(TAG, "2단계: MainActivity 및 MainFragment 상호작용 테스트 완료");
        Log.d(TAG, "testFullAppFlow_And_MainFragmentInteractions: 모든 테스트 단계 완료.");
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