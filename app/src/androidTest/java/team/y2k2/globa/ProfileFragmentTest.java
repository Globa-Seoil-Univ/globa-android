package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard; // 추가
import static androidx.test.espresso.action.ViewActions.replaceText;    // 추가
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo; // scrollTo 사용
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra; // hasExtra 추가
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException; // NoMatchingViewException import
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
import team.y2k2.globa.main.profile.alert.AlertActivity;
import team.y2k2.globa.main.profile.edit.NicknameEditActivity;
import team.y2k2.globa.main.profile.inquiry.add.InquiryAddActivity;
import team.y2k2.globa.main.profile.info.MyInfoActivity;
import team.y2k2.globa.main.profile.service_info.ServiceInfoActivity;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    private static final String TAG = "ProfileFragmentTest";

    // MainActivity UI 요소 ID
    private static final int mainActivityProfileTabId = R.id.item_main_profile;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main; // MainActivity의 FragmentContainerView ID
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity의 홈 화면(MainFragment) 확인용

    // ProfileFragment UI 요소 ID
    private static final int profileUsernameTextViewId = R.id.textview_profile_account_username;
    private static final int profileUserCodeTextViewId = R.id.textview_profile_account_user_code;
    private static final int profileImageViewId = R.id.imageview_profile_account_image;
    private static final int profileUserInfoLayoutId = R.id.relativelayout_profile_account_user;
    private static final int profileSettingsRecyclerViewId = R.id.recyclerview_profile_setting;

    // SettingItemAdapter 아이템 내 타이틀 ID
    private static final int itemSettingTitleId = R.id.textview_item_setting_title;

    // MyInfoActivity의 RecyclerView 및 아이템 관련 ID
    private static final int myInfoRecyclerViewId = R.id.recyclerview_my_info_items;
    private static final int myInfoItemListLayoutId = R.id.constraintlayout_my_info_item_list;
    private static final int myInfoItemListTitleId = R.id.textview_my_info_item_list_title;

    // NicknameEditActivity 내 View ID
    private static final int nicknameEditInputNameId = R.id.edittext_nickname_edit_input_name;
    private static final int nicknameEditChangeButtonId = R.id.textview_nickname_edit_change;


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
    public void testProfileFragment_UI_and_Interactions_AfterFullFlow() {
        Log.d(TAG, "testProfileFragment_UI_and_Interactions_AfterFullFlow: 테스트 시작");

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


        // === 2단계: MainActivity에서 프로필 탭으로 이동 ===
        Log.d(TAG, "  2단계: 프로필 탭으로 이동 시작");
        Log.d(TAG, "    MainActivity UI 안정화 대기 중...");
        try {
            Thread.sleep(1000); // MainActivity UI 안정화 및 탭 클릭 가능 상태 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "    Thread.sleep 중단됨 (MainActivity UI 안정화 대기 중)", e);
            return;
        }
        Log.d(TAG, "    프로필 탭(" + mainActivityProfileTabId + ") 표시 확인 및 클릭 시도");
        onView(withId(mainActivityProfileTabId)).check(matches(isDisplayed()));
        onView(withId(mainActivityProfileTabId)).perform(click());

        Log.d(TAG, "    ProfileFragment 사용자 정보 로딩 대기 중...");
        try {
            Thread.sleep(3000); // ProfileFragment API 호출 및 UI 업데이트 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (ProfileFragment 로딩 대기 중)", e);
            Thread.currentThread().interrupt();
            return;
        }
        Log.d(TAG, "  2단계: 프로필 탭으로 이동 및 정보 로딩 확인 (가정)");

        // === 3단계: ProfileFragment 초기 UI 및 데이터 표시 검증 ===
        Log.d(TAG, "  3단계: ProfileFragment 초기 UI 및 데이터 표시 검증 시작");
        onView(withId(profileUsernameTextViewId)).check(matches(isDisplayed())).check(matches(not(withText(""))));
        onView(withId(profileUserCodeTextViewId)).check(matches(isDisplayed())).check(matches(not(withText(""))));
        onView(withId(profileImageViewId)).check(matches(isDisplayed()));
        onView(withId(profileSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  3단계: ProfileFragment 초기 UI 및 데이터 표시 검증 완료");

        // === 4단계: 내 정보 수정 영역 클릭 -> MyInfoActivity -> NicknameEditActivity -> 이름 변경 -> 결과 확인 ===
        Log.d(TAG, "  4단계: 내 정보 수정 영역 클릭 및 이름 변경 테스트 시작");

        onView(withId(profileUserInfoLayoutId)).perform(click());
        intended(hasComponent(MyInfoActivity.class.getName()));
        Log.d(TAG, "    MyInfoActivity 실행 확인됨.");

        Log.d(TAG, "    MyInfoActivity 내부 'Name' 항목 클릭 대기 및 시도...");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        String nameItemLabelInMyInfo = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.name);
        onView(withId(myInfoRecyclerViewId))
                .perform(scrollTo(hasDescendant(allOf(withId(myInfoItemListTitleId), withText(nameItemLabelInMyInfo)))));
        onView(allOf(withId(myInfoItemListLayoutId), hasDescendant(allOf(withId(myInfoItemListTitleId), withText(nameItemLabelInMyInfo)))))
                .perform(click());

        intended(hasComponent(NicknameEditActivity.class.getName()));
        Log.d(TAG, "    NicknameEditActivity 실행 확인됨.");

        String newTestName = "Espresso 새이름 " + System.currentTimeMillis() % 100;
        Log.d(TAG, "    NicknameEditActivity에서 새 이름 입력: " + newTestName);
        try {
            Thread.sleep(1000);
            onView(withId(nicknameEditInputNameId)).perform(replaceText(newTestName), closeSoftKeyboard());
            onView(withId(nicknameEditChangeButtonId)).perform(click());
        } catch (InterruptedException e) {
            Log.e(TAG, "    NicknameEditActivity와 상호작용 중 Thread.sleep 중단됨: " + e.getMessage());
            Thread.currentThread().interrupt(); return;
        } catch (Exception e) {
            Log.e(TAG, "    NicknameEditActivity와 상호작용 중 오류 발생: " + e.getMessage());
            throw e;
        }

        Log.d(TAG, "    ProfileFragment로 돌아와 이름 업데이트 대기 중...");
        try {
            Thread.sleep(3000); // API 호출, Activity 종료, 결과 처리, UI 업데이트에 충분한 시간 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }

        Log.d(TAG, "  4단계: 내 정보 수정 영역 클릭 및 이름 변경 테스트 완료");

        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String currentUserId = prefs.getString("userId", null);
        assertNotNull("설정 항목 테스트를 위한 userId는 null이 아니어야 합니다.", currentUserId);

        Log.d(TAG, "  5단계: 설정 항목('알림 설정') 클릭 테스트 시작");
        String alertSettingText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.profile_alert_setting);
//        onView(withId(profileSettingsRecyclerViewId)).perform(scrollTo(hasDescendant(withText(alertSettingText))));
//        onView(allOf(withId(itemSettingTitleId), withText(alertSettingText))).perform(click());
        intended(allOf(hasComponent(AlertActivity.class.getName()), hasExtra("userId", currentUserId)));
        Log.d(TAG, "    AlertActivity로 userId와 함께 정상 이동 확인");
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  5단계: 설정 항목('알림 설정') 클릭 테스트 완료");

        Log.d(TAG, "  6단계: 설정 항목('문의') 클릭 테스트 시작");
        String inquiryText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.profile_inquiry);
        onView(withId(profileSettingsRecyclerViewId)).perform(scrollTo(hasDescendant(withText(inquiryText))));
        onView(allOf(withId(itemSettingTitleId), withText(inquiryText))).perform(click());
        intended(allOf(hasComponent(InquiryAddActivity.class.getName()), hasExtra("userId", currentUserId)));
        Log.d(TAG, "    InquiryActivity로 userId와 함께 정상 이동 확인");
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  6단계: 설정 항목('문의') 클릭 테스트 완료");

        Log.d(TAG, "  7단계: 설정 항목('서비스 정보') 클릭 테스트 시작");
        String serviceInfoText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.profile_service_info);
        onView(withId(profileSettingsRecyclerViewId)).perform(scrollTo(hasDescendant(withText(serviceInfoText))));
        onView(allOf(withId(itemSettingTitleId), withText(serviceInfoText))).perform(click());
        intended(allOf(hasComponent(ServiceInfoActivity.class.getName()), hasExtra("userId", currentUserId)));
        Log.d(TAG, "    ServiceInfoActivity로 userId와 함께 정상 이동 확인");
        pressBack();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  7단계: 설정 항목('서비스 정보') 클릭 테스트 완료");

        Log.d(TAG, "testProfileFragment_UI_and_Interactions_AfterFullFlow: 모든 테스트 완료");
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
        Log.d(TAG, "    [" + loginType + "] 저장된 UID (SNS): " + uid);
        Log.d(TAG, "    [" + loginType + "] 저장된 이름(로그인 시점 - 참고용): " + name);
        Log.d(TAG, "    [" + loginType + "] 저장된 userName (ProfileFragment API): " + userName);
        Log.d(TAG, "    [" + loginType + "] 저장된 userId (ProfileFragment API): " + userId);

        assertNotNull("[" + loginType + "] 로그인 성공 후 Access token은 null이 아니어야 합니다.", accessToken);
        assertTrue("[" + loginType + "] 로그인 성공 후 Access token은 비어있지 않아야 합니다.", !TextUtils.isEmpty(accessToken));
        assertNotNull("[" + loginType + "] 로그인 성공 후 Refresh token은 null이 아니어야 합니다.", refreshToken);
        assertTrue("[" + loginType + "] 로그인 성공 후 Refresh token은 비어있지 않아야 합니다.", !TextUtils.isEmpty(refreshToken));
        assertNotNull("[" + loginType + "] UID는 null이 아니어야 합니다.", uid);
        assertTrue("[" + loginType + "] UID는 비어있지 않아야 합니다.", !TextUtils.isEmpty(uid));

        assertNotNull("[" + loginType + "] 이름(SNS 로그인 시)은 null이 아니어야 합니다.", name);
        assertTrue("[" + loginType + "] 이름(SNS 로그인 시)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(name));
        assertNotNull("[" + loginType + "] 사용자 이름(ProfileFragment에서 저장)은 null이 아니어야 합니다.", userName);
        assertTrue("[" + loginType + "] 사용자 이름(ProfileFragment에서 저장)은 비어있지 않아야 합니다.", !TextUtils.isEmpty(userName));
        assertNotNull("[" + loginType + "] 사용자 ID(ProfileFragment에서 저장)는 null이 아니어야 합니다.", userId);
        assertTrue("[" + loginType + "] 사용자 ID(ProfileFragment에서 저장)는 비어있지 않아야 합니다.", !TextUtils.isEmpty(userId));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }
            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    Log.w(TAG, "atPosition: 위치 " + position + "의 ViewHolder를 찾을 수 없음.");
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}