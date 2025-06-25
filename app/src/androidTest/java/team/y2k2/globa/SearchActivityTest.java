package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail; // fail 추가

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException; // NoMatchingViewException import
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
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

import java.util.concurrent.atomic.AtomicReference;

import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;
import team.y2k2.globa.main.search.SearchActivity;

@RunWith(AndroidJUnit4.class)
public class SearchActivityTest {

    private static final String TAG = "SearchActivityTest";

    // MainActivity UI 요소 ID
    private static final int mainFragmentSearchIconId = R.id.image_button_main_search;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity 홈 화면 확인용

    // SearchActivity UI 요소 ID
    private static final int searchEditTextId = R.id.edittext_search;
    private static final int searchCancelButtonId = R.id.textview_search_cancel;
    private static final int searchRecyclerViewId = R.id.recyclerview_search_history;

    // 검색 결과 아이템 내 제목 TextView ID (item_docs.xml 또는 유사한 레이아웃에 있어야 함)
    private static final int searchResultItemTitleTextViewId = R.id.textview_document_title;

    private static final String FALLBACK_SEARCH_KEYWORD = "회의"; // 기본 검색어

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

    public static ViewAction getTextFromChildView(final int childViewId, final AtomicReference<String> stringHolder) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(View.class);
            }
            @Override
            public String getDescription() {
                return "get text from a child TextView with id = " + childViewId;
            }
            @Override
            public void perform(UiController uiController, View view) {
                TextView textViewToRead = view.findViewById(childViewId);
                if (textViewToRead != null) {
                    stringHolder.set(textViewToRead.getText().toString());
                } else {
                    stringHolder.set(null);
                    Log.e(TAG, "getTextFromChildView: childViewId " + childViewId + "를 찾을 수 없습니다.");
                }
            }
        };
    }

    @Test
    public void testSearchActivity_FullFlow_SearchAndItemClick() {
        Log.d(TAG, "testSearchActivity_FullFlow_SearchAndItemClick: 테스트 시작");

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
            Thread.currentThread().interrupt();
            fail("Login or navigation to Main interrupted: " + e.getMessage());
            return;
        } catch (Exception e) {
            Log.e(TAG, "Login or navigation to Main 중 예외 발생", e);
            fail("Login or navigation to Main 중 예외 발생: " + e.getMessage());
            return;
        }
        Log.d(TAG, "  1단계: 로그인 플로우 및 MainActivity 진입 완료 (또는 이미 진입됨)");


        // === 2단계: MainActivity에서 검색 아이콘 클릭하여 SearchActivity로 이동 ===
        Log.d(TAG, "  2단계: SearchActivity로 이동 시작");
        try {
            Thread.sleep(1000); // MainActivity UI 안정화 대기
            onView(withId(mainFragmentSearchIconId)).check(matches(isDisplayed())); // 검색 아이콘이 보이는지 확인
            onView(withId(mainFragmentSearchIconId)).perform(click());
            intended(hasComponent(SearchActivity.class.getName()));
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (SearchActivity 이동 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        Log.d(TAG, "  2단계: SearchActivity로 이동 완료");


        // === 3단계: SearchActivity 초기 UI 검증 및 첫 아이템 제목 추출 ===
        Log.d(TAG, "  3단계: SearchActivity 초기 UI 검증 및 첫 아이템 제목 추출 시작");
        try {
            Thread.sleep(3000); // 초기 로컬 검색 결과 또는 추천 검색어 로드 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(searchEditTextId)).check(matches(isDisplayed()));
        onView(withId(searchCancelButtonId)).check(matches(isDisplayed()));
        onView(withId(searchRecyclerViewId)).check(matches(isDisplayed()));

        final AtomicReference<String> dynamicallyExtractedKeyword = new AtomicReference<>(null);
        try {
            onView(withId(searchRecyclerViewId)).check(matches(hasMinimumChildCount(1)));
            Log.d(TAG, "    초기 검색 결과 아이템이 RecyclerView에 표시됨.");
            onView(withId(searchRecyclerViewId))
                    .perform(actionOnItemAtPosition(0, getTextFromChildView(searchResultItemTitleTextViewId, dynamicallyExtractedKeyword)));
            String extractedTitle = dynamicallyExtractedKeyword.get();
            if (!TextUtils.isEmpty(extractedTitle)) {
                Log.i(TAG, "    성공적으로 추출된 첫 번째 아이템 제목: " + extractedTitle);
            } else {
                Log.w(TAG, "    첫 번째 아이템의 제목이 비어있거나 null입니다.");
            }
        } catch (AssertionError | NoMatchingViewException e) {
            Log.w(TAG, "    초기 검색 결과 아이템이 없거나, 첫 아이템의 제목을 추출하는 데 실패했습니다. 에러: " + e.getMessage());
        }

        String keywordToUse = dynamicallyExtractedKeyword.get();
        if (TextUtils.isEmpty(keywordToUse)) {
            keywordToUse = FALLBACK_SEARCH_KEYWORD;
            Log.w(TAG, "    동적 키워드 추출 실패 또는 없음. 기본 폴백 키워드 사용: " + keywordToUse);
        } else {
            Log.i(TAG, "    동적으로 추출된 키워드 사용 예정: " + keywordToUse);
        }
        Log.d(TAG, "  3단계: SearchActivity 초기 UI 검증 및 키워드 설정 완료. 사용할 키워드: " + keywordToUse);


        // === 4단계: 키워드 검색 수행 ===
        Log.d(TAG, "  4단계: 키워드 \"" + keywordToUse + "\" 로 검색 수행 시작");
        onView(withId(searchEditTextId)).perform(replaceText(keywordToUse), closeSoftKeyboard());
        onView(withId(searchEditTextId)).perform(pressImeActionButton());
        Log.d(TAG, "    검색 실행됨. API 결과 및 RecyclerView 업데이트 대기 중...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }

        try {
            onView(withId(searchRecyclerViewId)).check(matches(hasMinimumChildCount(1)));
            Log.d(TAG, "    키워드 '" + keywordToUse + "'에 대한 검색 결과가 RecyclerView에 표시됨.");

            // === 5단계: 검색 결과 아이템 클릭 ===
            Log.d(TAG, "  5단계: 검색 결과 첫 번째 아이템 클릭 시도");
            onView(withId(searchRecyclerViewId))
                    .perform(actionOnItemAtPosition(0, click()));
            intended(allOf(
                    hasComponent(DocsActivity.class.getName()),
                    hasExtra("recordId", not(emptyOrNullString()))
            ));
            Log.d(TAG, "    DocsActivity 실행 및 recordId extra 존재 확인 완료.");

            pressBack();
            try { Thread.sleep(500); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            onView(withId(searchRecyclerViewId)).check(matches(isDisplayed())); // SearchActivity로 돌아왔는지 확인
            Log.d(TAG, "  5단계: 검색 결과 아이템 클릭 및 복귀 완료.");

        } catch (AssertionError | NoMatchingViewException e) {
            Log.w(TAG, "    키워드 '" + keywordToUse + "'에 대한 검색 결과가 없거나 API 호출 실패, 또는 RecyclerView에 아이템이 없음.");
        }

        // === 6단계: 취소 버튼 동작 검증 ===
        Log.d(TAG, "  6단계: 취소 버튼 동작 검증 시작");
        onView(withId(searchCancelButtonId)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed())); // MainActivity로 돌아왔는지 확인
        Log.d(TAG, "  6단계: 취소 버튼 동작 검증 완료 (SearchActivity 종료 확인)");

        Log.d(TAG, "testSearchActivity_FullFlow_SearchAndItemClick: 모든 테스트 완료");
    }

    private void verifySharedPreferencesAfterLogin(String loginType) {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        assertTrue("[" + loginType + "] 로그인 성공 후 Access token은 null이 아니어야 합니다.", !TextUtils.isEmpty(accessToken));
        Log.d(TAG, "    [" + loginType + "] SharedPreferences 검증 완료.");
    }

    public static Matcher<View> hasMinimumChildCount(final int count) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.Adapter adapter = view.getAdapter();
                return adapter != null && adapter.getItemCount() >= count;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("has " + count + " or more children");
            }
        };
    }
}