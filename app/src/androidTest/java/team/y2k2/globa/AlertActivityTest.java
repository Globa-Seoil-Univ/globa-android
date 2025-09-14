package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail; // fail 추가

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;
import team.y2k2.globa.main.profile.SettingItemAdapter;
import team.y2k2.globa.main.profile.alert.AlertActivity;


@RunWith(AndroidJUnit4.class)
public class AlertActivityTest {

    private static final String TAG = "AlertActivityTest";

    // MainActivity UI 요소 ID
    private static final int mainActivityProfileTabId = R.id.item_main_profile;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main; // MainActivity의 FragmentContainerView
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity의 홈 화면을 나타내는 View ID (예: 로고)


    // ProfileFragment UI 요소 ID
    private static final int profileFragmentSettingsRecyclerViewId = R.id.recyclerview_profile_setting;
    private static final int profileSettingItemTitleId = R.id.textview_item_setting_title;

    // AlertActivity UI 요소 ID
    private static final int alertActivityBackButtonId = R.id.image_button_alert_back;
    private static final int alertActivityRecyclerViewId = R.id.recyclerview_alert;

    // item_alert.xml UI 요소 ID
    private static final int alertItemSwitchId = R.id.switch_alert;

    private final boolean[] initialToggleStates = new boolean[3];
    private final boolean[] targetToggleStates = new boolean[3];

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

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isAssignableFrom(View.class); } // 부모 뷰가 어떤 타입이든 가능하도록 수정
            @Override public String getDescription() { return "Click on a child view with specified id."; }
            @Override public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) { v.performClick(); }
            }
        };
    }

    public static ViewAction getSwitchState(final int switchId, final boolean[] stateArray, final int arrayIndex) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isAssignableFrom(View.class); }
            @Override public String getDescription() { return "Get state from SwitchCompat with id " + switchId; }
            @Override public void perform(UiController uiController, View view) {
                SwitchCompat switchCompat = view.findViewById(switchId);
                if (switchCompat != null) { stateArray[arrayIndex] = switchCompat.isChecked(); }
            }
        };
    }

    private void performLoginAndNavigateToMain() {
        Log.d(TAG, "performLoginAndNavigateToMain: 시작");
        try {
            boolean onMainActivityAlready = false;
            try {
                // IntroActivity가 빠르게 MainActivity로 리다이렉트하는 경우를 위해 짧게 대기
                Thread.sleep(2000); // 앱 초기화 및 자동 로그인/리다이렉션 시간 부여
                onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
                // 위 라인에서 예외가 발생하지 않으면 MainActivity가 이미 표시된 것임
                Log.d(TAG, "MainActivity가 이미 표시됨. 로그인 플로우 건너뜀.");
                onMainActivityAlready = true;
            } catch (NoMatchingViewException e) {
                // MainActivity가 즉시 보이지 않으면, IntroActivity부터 시작하는 전체 로그인 플로우 진행
                Log.d(TAG, "MainActivity가 즉시 표시되지 않음. 인트로/로그인 플로우 진행.");
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                fail("Interrupted while checking for initial MainActivity: " + ie.getMessage());
                return; // 테스트 중단
            }

            if (!onMainActivityAlready) {
                // IntroActivity는 ActivityScenarioRule에 의해 이미 실행된 상태
                // IntroActivity의 UI 요소가 완전히 로드될 시간을 추가로 부여 (위의 2초가 부족했을 경우 대비)
                Thread.sleep(3000);
                onView(withId(R.id.button_intro_bottom_start)).perform(click());
                Thread.sleep(1000); // LoginActivity 로드 대기

                onView(withId(R.id.button_sign_in_google)).perform(click());
                Log.d(TAG, "    수동 Google 로그인 대기 중... (20초)");
                Thread.sleep(20000); // 수동 로그인 및 MainActivity로의 전환 시간

                intended(hasComponent(MainActivity.class.getName())); // MainActivity 실행되었는지 확인
            }

            // MainActivity에 도달했으므로, UI가 안정화되고 홈 화면(또는 기본 프래그먼트)이 표시될 때까지 대기
            onView(withId(mainActivityHomeIndicatorId)).check(matches(isDisplayed()));
            // 필요시 특정 탭을 클릭하여 초기 상태를 일관되게 만들 수 있음
            // 예: onView(withId(mainActivityHomeTabId)).perform(click());
            // Thread.sleep(500);

            verifySharedPreferencesAfterLogin("Google");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Login or navigation to Main interrupted: " + e.getMessage());
        } catch (Exception e) { // NoActivityResumedException 등 다른 Espresso 예외 처리
            Log.e(TAG, "Exception in performLoginAndNavigateToMain", e);
            // 실패 시 현재 화면의 뷰 계층 구조를 덤프하면 디버깅에 도움이 될 수 있습니다 (별도 설정 필요).
            fail("Exception in performLoginAndNavigateToMain: " + e.getMessage() +
                    " - Check if MainActivity launched and resumed correctly after login/redirect.");
        }
        Log.d(TAG, "  로그인 및 MainActivity(MainFragment 활성화) 진입 완료 (또는 이미 진입됨)");
    }


    @Test
    public void testAlertSettings_ToggleAndPersist() {
        Log.d(TAG, "testAlertSettings_ToggleAndPersist: 테스트 시작");

        // === 1단계: 로그인 플로우 및 MainActivity 진입 ===
        performLoginAndNavigateToMain(); // 수정된 로그인/메인 진입 메소드 호출

        // === 2단계: MainActivity에서 프로필 탭으로 이동 ===
        navigateToProfileFragment();

        // === 3단계: ProfileFragment에서 "알림 설정" 항목 클릭하여 AlertActivity로 이동 ===
        navigateToAlertActivity(1); // 첫 번째 호출이므로 times(1) 기대

        // === 4단계: AlertActivity 초기 데이터 로드 대기 및 초기 토글 상태 기록 ===
        Log.d(TAG, "  4단계: AlertActivity 초기 데이터 로드 및 토글 상태 기록");
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        for (int i = 0; i < 3; i++) {
            final int position = i;
            onView(withId(alertActivityRecyclerViewId))
                    .perform(actionOnItemAtPosition(position, getSwitchState(alertItemSwitchId, initialToggleStates, position)));
            Log.d(TAG, "    초기 토글 상태 [" + position + "]: " + initialToggleStates[position]);
            targetToggleStates[position] = !initialToggleStates[position];
        }

        // === 5단계: 모든 토글 상태 변경 ===
        Log.d(TAG, "  5단계: 모든 토글 상태 변경");
        for (int i = 0; i < 3; i++) {
            onView(withId(alertActivityRecyclerViewId))
                    .perform(actionOnItemAtPosition(i, clickChildViewWithId(alertItemSwitchId)));
            Log.d(TAG, "    토글 [" + i + "] 클릭 -> 목표 상태 " + targetToggleStates[i]);
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // === 6단계: 뒤로가기 버튼 클릭 (설정 저장) ===
        Log.d(TAG, "  6단계: 뒤로가기 버튼 클릭 (설정 저장)");
        onView(withId(alertActivityBackButtonId)).perform(click());
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    ProfileFragment로 복귀 확인됨.");

        // === 7단계: AlertActivity 재진입 ===
        Log.d(TAG, "  7단계: AlertActivity 재진입");
        navigateToAlertActivity(2); // 두 번째 호출이므로 times(2) 기대

        // === 8단계: AlertActivity 데이터 로드 대기 ===
        Log.d(TAG, "  8단계: AlertActivity 재진입 후 데이터 로드 대기");
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

        // === 9단계: 변경된 토글 상태 검증 ===
        Log.d(TAG, "  9단계: 변경된 토글 상태 검증");
        for (int i = 0; i < 3; i++) {
            final int position = i;
            final boolean expectedState = targetToggleStates[position];
            onView(withId(alertActivityRecyclerViewId))
                    .perform(actionOnItemAtPosition(position, new ViewAction() {
                        @Override public Matcher<View> getConstraints() { return isAssignableFrom(View.class); }
                        @Override public String getDescription() { return "verify switch state"; }
                        @Override public void perform(UiController uiController, View view) {
                            SwitchCompat switchCompat = view.findViewById(alertItemSwitchId);
                            assertEquals("토글 [" + position + "] 상태 불일치", expectedState, switchCompat.isChecked());
                            Log.d(TAG, "    검증: 토글 [" + position + "] 상태: " + switchCompat.isChecked() + " (예상: " + expectedState + ")");
                        }
                    }));
        }
        Log.d(TAG, "  9단계: 변경된 토글 상태 검증 완료.");
        Log.d(TAG, "testAlertSettings_ToggleAndPersist: 모든 테스트 완료");
    }

    private void navigateToProfileFragment() {
        Log.d(TAG, "    ProfileFragment로 이동 중...");
        onView(withId(mainActivityProfileTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    ProfileFragment로 이동 완료.");
    }

    private void navigateToAlertActivity(int expectedIntentCount) {
        Log.d(TAG, "    AlertActivity로 이동 중... (예상 Intent 수: " + expectedIntentCount + ")");
        String alertSettingsText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.profile_alert_setting);
        onView(withId(profileFragmentSettingsRecyclerViewId))
                .perform(scrollToHolder(isHolderWithTitle(alertSettingsText)));
        onView(allOf(withId(profileSettingItemTitleId), withText(alertSettingsText)))
                .perform(click());

        // 앱 코드에서 "알림 설정" 클릭 시 AlertActivity 인텐트가 1번만 발생하도록 수정되었다고 가정합니다.
        // 만약 여전히 2번 발생한다면, 이 부분은 expectedIntentCount에 맞게 조정되거나,
        // 앱 코드 수정이 우선되어야 합니다.
        intended(hasComponent(AlertActivity.class.getName()), Intents.times(expectedIntentCount));
        Log.d(TAG, "    AlertActivity로 이동 및 Intent " + expectedIntentCount + "회 발생 확인 완료.");
    }

    public static Matcher<RecyclerView.ViewHolder> isHolderWithTitle(final String title) {
        return new BoundedMatcher<RecyclerView.ViewHolder, SettingItemAdapter.AdapterViewHolder>(SettingItemAdapter.AdapterViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("ViewHolder with title: " + title);
            }
            @Override
            protected boolean matchesSafely(SettingItemAdapter.AdapterViewHolder item) {
                TextView titleTextView = item.itemView.findViewById(profileSettingItemTitleId);
                return titleTextView != null && title.equals(titleTextView.getText().toString());
            }
        };
    }

    private void verifySharedPreferencesAfterLogin(String loginType) {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        assertTrue("[" + loginType + "] 로그인 성공 후 Access token은 null이 아니어야 합니다.", !TextUtils.isEmpty(accessToken));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }
}