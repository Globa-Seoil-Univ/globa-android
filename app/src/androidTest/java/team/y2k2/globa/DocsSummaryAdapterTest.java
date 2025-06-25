package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
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

import team.y2k2.globa.EspressoIdlingResource;
import team.y2k2.globa.R;
import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DocsSummaryAdapterTest {

    private static final String TAG = "DocsSummaryAdapterTest";

    // MainFragment UI 요소 ID (문서 목록 접근용)
    private static final int mainFragmentDocsRecyclerViewId = R.id.recyclerview_main_document;
    // MainActivity의 Fragment 컨테이너 ID 및 홈 화면(MainFragment) 활성화 확인용 View ID
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // 실제 앱의 ID로 확인 필요


    // DocsActivity UI 요소 ID
    private static final int docsActivitySummaryButtonId = R.id.button_docs_summary;
    private static final int docsActivityMainRecyclerViewId = R.id.recyclerview_docs_detail;

    // DocsSummaryAdapter 아이템 (item_docs_summary.xml) 내부 View ID
    private static final int itemSummaryTitleId = R.id.textview_item_docs_summary_title;
    private static final int itemSummaryTimeId = R.id.textview_item_docs_summary_time;
    private static final int itemSummaryNestedRecyclerViewId = R.id.recyclerview_item_docs_summary_description;

    // DocsSummaryDescriptionAdapter 아이템 (item_docs_summary_description.xml) 내부 View ID
    private static final int nestedItemSummaryDescriptionTextId = R.id.textview_item_docs_summary_description;


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
    public void testDocsSummaryAdapter_ItemContentDisplay_AfterFullFlow() {
        Log.d(TAG, "testDocsSummaryAdapter_ItemContentDisplay_AfterFullFlow: 테스트 시작");

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
        intended(hasComponent(DocsActivity.class.getName())); // DocsActivity가 1번 실행되었는지 확인

        // === 3단계: DocsActivity에서 "요약" 탭으로 전환 ===
        Log.d(TAG, "  3단계: DocsActivity에서 '요약' 탭으로 전환 시작");
        try {
            Thread.sleep(5000); // API 응답 및 기본 UI 구성 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (DocsActivity 기본 UI 로딩 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(docsActivitySummaryButtonId)).perform(click());
        Log.d(TAG, "    '요약' 탭 클릭됨. DocsSummaryAdapter 데이터 로딩 대기 중...");
        try {
            Thread.sleep(3000); // 요약 데이터로 어댑터 교체 및 바인딩 시간 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (DocsSummaryAdapter 데이터 로딩 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        Log.d(TAG, "  3단계: DocsActivity에서 '요약' 탭으로 전환 완료");


        // === 4단계: DocsSummaryAdapter 아이템 내용 표시 확인 ===
        Log.d(TAG, "  4단계: DocsSummaryAdapter 아이템 내용 표시 확인 시작");
        Log.d(TAG, "    RecyclerView 및 첫 번째 아이템의 제목, 시간, 중첩된 RecyclerView의 본문 표시 여부 및 내용 비어있지 않음 확인");
        onView(withId(docsActivityMainRecyclerViewId)) // DocsActivity 내부의 메인 RecyclerView
                .check(matches(isDisplayed()))
                .check(matches(atPosition(0, allOf( // RecyclerView의 첫 번째 아이템 (DocsSummaryAdapter의 아이템)
                        hasDescendant(allOf(withId(itemSummaryTitleId), isDisplayed(), not(withText("")))),
                        hasDescendant(allOf(withId(itemSummaryTimeId), isDisplayed(), not(withText("")))),
                        hasDescendant(allOf(
                                withId(itemSummaryNestedRecyclerViewId), // 중첩된 RecyclerView
                                isDisplayed(),
                                // 중첩된 RecyclerView의 첫 번째 아이템(DocsSummaryDescriptionAdapter의 아이템)의 TextView가 표시되고 내용이 있는지 확인
                                atPosition(0, hasDescendant(allOf(withId(nestedItemSummaryDescriptionTextId), isDisplayed(), not(withText("")))))
                        ))
                ))));

        Log.d(TAG, "  4단계: DocsSummaryAdapter 아이템 내용 표시 확인 완료");
        Log.d(TAG, "testDocsSummaryAdapter_ItemContentDisplay_AfterFullFlow: 테스트 완료");

        pressBack(); // DocsActivity 종료
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