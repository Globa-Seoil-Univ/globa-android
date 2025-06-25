package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack; // pressBack() 사용 시 필요
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast; // Toast import 추가 (테스트 코드에서는 직접 사용하지 않음)

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
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
import team.y2k2.globa.main.profile.SettingItemAdapter;
import team.y2k2.globa.main.profile.inquiry.InquiryActivity;

@RunWith(AndroidJUnit4.class)
public class InquiryActivityTest {

    private static final String TAG = "InquiryActivityTest";

    // MainActivity UI 요소 ID
    private static final int mainActivityProfileTabId = R.id.item_main_profile;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity 홈 화면 확인용

    // ProfileFragment UI 요소 ID
    private static final int profileFragmentSettingsRecyclerViewId = R.id.recyclerview_profile_setting;
    private static final int profileSettingItemTitleId = R.id.textview_item_setting_title;

    // InquiryActivity UI 요소 ID
    private static final int inquiryActivityLayoutId = R.id.constraintlayout_inquiry;
    private static final int inquiryBackButtonId = R.id.imageview_inquiry_top_back;
    private static final int inquiryTitleTextViewId = R.id.textview_inquiry_top_title;
    private static final int inquiryConfirmButtonId = R.id.textview_inquiry_top_confirm;
    private static final int inquiryInputTitleId = R.id.edittext_inquiry_title;
    private static final int inquiryInputDescriptionId = R.id.edittext_inquiry_description;

    @Rule
    public ActivityScenarioRule<IntroActivity> activityRule = new ActivityScenarioRule<>(IntroActivity.class);

    // Intent 호출 횟수를 추적하기 위한 변수
    private int inquiryActivityLaunchAttempt;


    @Before
    public void setUp() {
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        Log.d(TAG, "setUp: 테스트 준비 완료");
        inquiryActivityLaunchAttempt = 0; // 각 테스트 시작 시 초기화
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        Intents.release();
        Log.d(TAG, "tearDown: 테스트 환경 정리 완료");
    }

    @Test
    public void testInquiryActivity_SubmitAndBackNavigation() {
        Log.d(TAG, "testInquiryActivity_SubmitAndBackNavigation: 테스트 시작");

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
                Thread.sleep(3000);
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


        navigateToProfileFragment();
        navigateToInquiryActivity();

        Log.d(TAG, "  초기 UI 검증 (타이틀 카운트 제외)");
        onView(withId(inquiryTitleTextViewId)).check(matches(withText(R.string.profile_inquiry)));
        onView(withId(inquiryBackButtonId)).check(matches(isDisplayed()));
        onView(withId(inquiryConfirmButtonId)).check(matches(isDisplayed()));
        onView(withId(inquiryInputTitleId)).check(matches(isDisplayed()));
        onView(withId(inquiryInputDescriptionId)).check(matches(isDisplayed()));
        Log.d(TAG, "    초기 UI 검증 완료.");

        Log.d(TAG, "  입력 없이 확인 버튼 클릭 시 Activity 유지 확인");
        onView(withId(inquiryConfirmButtonId)).perform(click());
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withId(inquiryActivityLayoutId)).check(matches(isDisplayed()));
        Log.d(TAG, "    빈 입력 시 Activity 유지 확인 완료.");

        Log.d(TAG, "  제목만 입력 후 확인 버튼 클릭 시 Activity 유지 확인");
        onView(withId(inquiryInputTitleId)).perform(replaceText("테스트 문의 제목"), closeSoftKeyboard());
        onView(withId(inquiryConfirmButtonId)).perform(click());
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withId(inquiryActivityLayoutId)).check(matches(isDisplayed()));
        Log.d(TAG, "    제목만 입력 시 Activity 유지 확인 완료.");
        onView(withId(inquiryInputTitleId)).perform(replaceText(""));

        Log.d(TAG, "  내용만 입력 후 확인 버튼 클릭 시 Activity 유지 확인");
        onView(withId(inquiryInputDescriptionId)).perform(replaceText("테스트 문의 내용입니다."), closeSoftKeyboard());
        onView(withId(inquiryConfirmButtonId)).perform(click());
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        onView(withId(inquiryActivityLayoutId)).check(matches(isDisplayed()));
        Log.d(TAG, "    내용만 입력 시 Activity 유지 확인 완료.");

        Log.d(TAG, "  성공적인 문의 제출 시도");
        onView(withId(inquiryInputTitleId)).perform(replaceText("정상적인 문의 제목"), closeSoftKeyboard());
        onView(withId(inquiryConfirmButtonId)).perform(click());
        Log.d(TAG, "    문의 제출됨. API 호출 및 Activity 종료 대기 중...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    성공적인 문의 제출 후 ProfileFragment로 복귀 확인.");

        Log.d(TAG, "  뒤로가기 버튼 동작 검증 (내용 있을 시)");
        navigateToInquiryActivity();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(inquiryInputTitleId)).perform(replaceText("뒤로가기 제목"), closeSoftKeyboard());
        onView(withId(inquiryInputDescriptionId)).perform(replaceText("뒤로가기 내용"), closeSoftKeyboard());
        onView(withId(inquiryBackButtonId)).perform(click());
        Log.d(TAG, "    뒤로가기 버튼 클릭됨 (내용 있음). API 호출 및 Activity 종료 대기 중...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    뒤로가기 버튼 (내용 있음) 후 ProfileFragment로 복귀 확인.");

        Log.d(TAG, "  뒤로가기 버튼 동작 검증 (내용 없을 시)");
        navigateToInquiryActivity();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(inquiryInputTitleId)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(inquiryInputDescriptionId)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(inquiryBackButtonId)).perform(click());
        Log.d(TAG, "    뒤로가기 버튼 클릭됨 (내용 없음). Activity 종료 대기 중...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    뒤로가기 버튼 (내용 없음) 후 ProfileFragment로 복귀 확인.");

        Log.d(TAG, "testInquiryActivity_SubmitAndBackNavigation: 모든 테스트 완료");
    }

    private void navigateToProfileFragment() {
        Log.d(TAG, "    ProfileFragment로 이동 중...");
        onView(withId(mainActivityProfileTabId)).perform(click());
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        onView(withId(profileFragmentSettingsRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "    ProfileFragment로 이동 완료.");
    }

    private void navigateToInquiryActivity() { // 파라미터 제거, 내부에서 카운트 관리
        inquiryActivityLaunchAttempt++;
        Log.d(TAG, "    InquiryActivity로 이동 중... (누적 예상 Intent 수: " + inquiryActivityLaunchAttempt + ")");
        String inquiryText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.profile_inquiry);

        onView(withId(profileFragmentSettingsRecyclerViewId))
                .perform(scrollToHolder(isHolderWithTitle(inquiryText)));
        onView(allOf(withId(profileSettingItemTitleId), withText(inquiryText)))
                .perform(click());

        intended(allOf(
                hasComponent(InquiryActivity.class.getName()),
                hasExtra("userId", not(emptyOrNullString()))
        ), Intents.times(inquiryActivityLaunchAttempt)); // 수정된 카운터 사용
        Log.d(TAG, "    InquiryActivity로 이동 및 Intent " + inquiryActivityLaunchAttempt + "회 발생 확인 완료.");
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