package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack; // pressBack 추가
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static org.hamcrest.CoreMatchers.not;
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

import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;
import team.y2k2.globa.main.search.SearchActivity; // SearchActivity import 추가
import team.y2k2.globa.notification.NotificationActivity; // NotificationActivity import 추가

/**
 * IntroActivity에서 시작하여 LoginActivity를 거쳐 MainActivity로 이동하고,
 * MainActivity의 하단 네비게이션 및 MainFragment 동작을 검증하는 통합 테스트 클래스입니다.
 * (Google 로그인, 수동 개입 필요)
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     * 각 테스트 메소드 실행 전에 호출됩니다.
     * Espresso Intents를 초기화하고 EspressoIdlingResource를 등록합니다.
     * SharedPreferences를 초기화하여 깨끗한 상태에서 테스트를 시작합니다.
     */
    @Before
    public void setUp() {
        Intents.init(); // Espresso Intents 초기화
        // EspressoIdlingResource 등록 (비동기 작업 대기)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * 각 테스트 메소드 실행 후에 호출됩니다.
     * Espresso Intents를 해제하고 EspressoIdlingResource를 해제합니다.
     * SharedPreferences를 초기화하여 테스트 환경을 정리합니다.
     */
    @After
    public void tearDown() {
        Intents.release(); // Espresso Intents 해제
        // EspressoIdlingResource 해제
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * IntroActivity 시작 -> 서버 확인 -> LoginActivity 이동 -> 수동 Google 로그인 -> MainActivity 이동
     * -> MainActivity 하단 네비게이션 탭 전환 및 프래그먼트 표시 확인
     * -> MainFragment 상단 버튼(검색, 알림) 및 카테고리 버튼 동작 확인
     * 전체 흐름을 하나의 테스트 메소드에서 검증합니다.
     *
     * !!! 중요 !!!
     * 이 테스트를 실행할 때, 화면에 나타나는 Google 로그인 UI를 통해
     * 테스트 실행자가 **수동으로 유효한 Google 계정 로그인을 완료**해야 합니다.
     */
    @Test
    public void testFullAppFlow_FromIntroToMainInteractions() {
        // === 로그인 플로우 시작 ===

        // 1. IntroActivity 시작
        Log.d("MainActivityTest", "Launching IntroActivity...");
        ActivityScenario<IntroActivity> introScenario = ActivityScenario.launch(IntroActivity.class);

        // 2. IntroActivity가 서버 상태를 확인하고 시작 버튼을 활성화할 때까지 대기
        Log.d("MainActivityTest", "Waiting for IntroActivity server check and button enable...");
        try {
            Thread.sleep(5000); // 서버 확인 대기 (IdlingResource 권장)
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // 3. IntroActivity의 시작 버튼 클릭 (ID 확인 및 수정 필요!)
        Log.d("MainActivityTest", "Clicking IntroActivity start button...");
        int introButtonId = R.id.button_intro_bottom_start; // 예시 ID, 실제 ID로 변경!
        onView(withId(introButtonId)).check(matches(isDisplayed())).check(matches(isEnabled())).perform(click());

        // 4. LoginActivity 확인 및 Google 로그인 버튼 클릭
        Log.d("MainActivityTest", "Checking LoginActivity and clicking Google Sign-In...");
        int googleSignInButtonId = R.id.button_sign_in_google;
        onView(withId(googleSignInButtonId)).check(matches(isDisplayed())).perform(click());

        // 5. !!! 수동 Google 로그인 진행 !!!
        // 6. 비동기 작업 대기
        Log.d("MainActivityTest", "Waiting for manual Google Sign-In and API calls...");
        try {
            Thread.sleep(20000); // 수동 로그인 및 API 통신 시간
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // 7. MainActivity 전환 확인 및 SharedPreferences 검증
        Log.d("MainActivityTest", "Verifying MainActivity and SharedPreferences...");
        intended(hasComponent(MainActivity.class.getName()));
        verifySharedPreferencesAfterLogin("Google"); // 로그인 정보 저장 확인

        // === MainActivity 테스트 시작 ===

        Log.d("MainActivityTest", "Starting MainActivity tests...");

        // View ID 정의 (실제 레이아웃과 일치하는지 확인 및 수정 필요)
        int bottomNavId = R.id.bottom_navigation_main_bottom;
        int folderTabId = R.id.item_main_folder;
        int statisticsTabId = R.id.item_main_statistics;
        int profileTabId = R.id.item_main_profile;
        int mainTabId = R.id.item_main_main;
        int folderFragmentViewId = R.id.fragment_folder;
        int statisticsFragmentViewId = R.id.fragment_statistics;
        int profileFragmentViewId = R.id.recyclerview_profile_setting;
        int mainFragmentViewId = R.id.fragment_main;
        int searchButtonId = R.id.image_button_main_search;
        int notificationButtonId = R.id.image_button_main_notification;
        int currentlyButtonId = R.id.button_main_docs_type_1;
        int allButtonId = R.id.button_main_docs_type_2;
        int shareButtonId = R.id.button_main_docs_type_3;
        int sharedButtonId = R.id.button_main_docs_type_4;

        // 8. MainActivity 초기 상태 확인
        Log.d("MainActivityTest", "Checking initial MainActivity state...");
        onView(withId(bottomNavId)).check(matches(isDisplayed()));
        onView(withId(mainTabId)).check(matches(isSelected()));
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed()));

        // 9. MainFragment 상단 버튼 테스트 (검색)
        Log.d("MainActivityTest", "Testing Search button...");
        onView(withId(searchButtonId)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(SearchActivity.class.getName()));
        pressBack();
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed())); // MainActivity로 돌아왔는지 확인
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 10. MainFragment 상단 버튼 테스트 (알림)
        Log.d("MainActivityTest", "Testing Notification button...");
        onView(withId(notificationButtonId)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(NotificationActivity.class.getName()));
        pressBack();
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed())); // MainActivity로 돌아왔는지 확인
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 11. MainFragment 카테고리 버튼 테스트 ('Currently')
        Log.d("MainActivityTest", "Testing 'Currently' category button...");
        onView(withId(currentlyButtonId)).check(matches(isDisplayed())).perform(click());
        // 클릭 후 UI 변경 확인 로직 추가 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 12. MainFragment 카테고리 버튼 테스트 ('전체')
        Log.d("MainActivityTest", "Testing 'All' category button...");
        onView(withId(allButtonId)).check(matches(isDisplayed())).perform(click());
        // 클릭 후 UI 변경 확인 로직 추가 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 13. MainFragment 카테고리 버튼 테스트 ('공유 문서')
        Log.d("MainActivityTest", "Testing 'Share' category button...");
        onView(withId(shareButtonId)).check(matches(isDisplayed())).perform(click());
        // 클릭 후 UI 변경 확인 로직 추가 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 14. MainFragment 카테고리 버튼 테스트 ('공유 받은 문서')
        Log.d("MainActivityTest", "Testing 'All' category button...");
        onView(withId(sharedButtonId)).check(matches(isDisplayed())).perform(click());
        // 클릭 후 UI 변경 확인 로직 추가 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // === 하단 네비게이션 테스트 ===

        // 15. 폴더 탭 클릭 및 확인
        Log.d("MainActivityTest", "Clicking Folder tab...");
        onView(withId(folderTabId)).perform(click());
        onView(withId(folderTabId)).check(matches(isSelected()));
        onView(withId(mainTabId)).check(matches(not(isSelected())));
        Log.d("MainActivityTest", "Checking FolderFragment display...");
        onView(withId(folderFragmentViewId)).check(matches(isDisplayed())); // ID 확인 및 수정 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 16. 통계 탭 클릭 및 확인
        Log.d("MainActivityTest", "Clicking Statistics tab...");
        onView(withId(statisticsTabId)).perform(click());
        onView(withId(statisticsTabId)).check(matches(isSelected()));
        onView(withId(folderTabId)).check(matches(not(isSelected())));
        Log.d("MainActivityTest", "Checking StatisticsFragment display...");
        onView(withId(statisticsFragmentViewId)).check(matches(isDisplayed())); // ID 확인 및 수정 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 17. 프로필 탭 클릭 및 확인
        Log.d("MainActivityTest", "Clicking Profile tab...");
        onView(withId(profileTabId)).perform(click());
        onView(withId(profileTabId)).check(matches(isSelected()));
        onView(withId(statisticsTabId)).check(matches(not(isSelected())));
        Log.d("MainActivityTest", "Checking ProfileFragment display...");
        onView(withId(profileFragmentViewId)).check(matches(isDisplayed())); // ID 확인 및 수정 필요
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 18. 다시 메인 탭 클릭 및 확인
        Log.d("MainActivityTest", "Clicking Main tab again...");
        onView(withId(mainTabId)).perform(click());
        onView(withId(mainTabId)).check(matches(isSelected()));
        onView(withId(profileTabId)).check(matches(not(isSelected())));
        Log.d("MainActivityTest", "Checking MainFragment display again...");
        onView(withId(mainFragmentViewId)).check(matches(isDisplayed())); // ID 확인 및 수정 필요
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

        Log.d("MainActivityTest", "[" + loginType + "] Stored Access Token: " + accessToken);
        Log.d("MainActivityTest", "[" + loginType + "] Stored UID: " + uid);

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
}
