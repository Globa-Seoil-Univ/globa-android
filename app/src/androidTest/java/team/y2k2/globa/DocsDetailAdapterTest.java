package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
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

import java.util.concurrent.atomic.AtomicReference;

import team.y2k2.globa.docs.DocsActivity;
import team.y2k2.globa.docs.more.DocsMoreActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.keyword.detail.KeywordDetailActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DocsDetailAdapterTest {

    private static final String TAG = "DocsDetailAdapterTest";

    // MainFragment UI 요소 ID
    private static final int mainFragmentDocsRecyclerViewId = R.id.recyclerview_main_document;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main;
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top;

    // DocsActivity UI 요소 ID
    private static final int docsActivityMoreButtonId = R.id.imageview_docs_more;
    private static final int docsActivityBackButtonId = R.id.image_button_docs_back;
    private static final int docsDetailItemRecyclerViewId = R.id.recyclerview_docs_detail;

    // DocsDetailAdapter 아이템 내부 View ID
    private static final int itemTitleId = R.id.textview_item_docs_detail_title;
    private static final int itemTimeId = R.id.textview_item_docs_detail_time;
    private static final int itemDescriptionId = R.id.textview_item_docs_detail_description;

    // 댓글 다이얼로그(dialog_comment.xml) 내부 View ID
    private static final int commentDialogRecyclerViewId = R.id.recyclerview_comment;
    private static final int commentDialogInputEditTextId = R.id.edittext_comment;
    private static final int commentDialogConfirmButtonId = R.id.image_button_comment_confirm;
    private static final int commentDialogTitleId = R.id.textview_comment_name;

    // 댓글 아이템(item_comment.xml) 내부 View ID
    private static final int itemCommentContentId = R.id.textview_item_comment_content;

    @Rule
    public ActivityScenarioRule<IntroActivity> activityRule = new ActivityScenarioRule<>(IntroActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        Log.d(TAG, "setUp: 테스트 준비 완료");
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        Intents.release();
        Log.d(TAG, "tearDown: 테스트 환경 정리 완료");
    }

    private void performLoginIfNeeded() {
        Log.d(TAG, "로그인 상태 확인 및 필요시 로그인 수행");
        try {
            boolean onMainActivityAlready = false;
            try {
                Thread.sleep(2000);
                onView(withId(mainActivityFragmentContainerId)).check(matches(isDisplayed()));
                Log.d(TAG, "    MainActivity가 이미 표시됨. 로그인 플로우 건너뜀.");
                onMainActivityAlready = true;
            } catch (NoMatchingViewException e) {
                Log.d(TAG, "    MainActivity가 즉시 표시되지 않음. 인트로/로그인 플로우 진행.");
            }

            if (!onMainActivityAlready) {
                Thread.sleep(3000);
                onView(withId(R.id.button_intro_bottom_start)).perform(click());
                Thread.sleep(1000);
                onView(withId(R.id.button_sign_in_google)).perform(click());
                Log.d(TAG, "    수동 Google 로그인 대기 중... (20초)");
                Thread.sleep(20000);
                intended(hasComponent(MainActivity.class.getName()));
            }
            onView(withId(mainActivityHomeIndicatorId)).check(matches(isDisplayed()));
            verifySharedPreferencesAfterLogin();
        } catch (Exception e) {
            fail("로그인 또는 MainActivity 진입 중 예외 발생: " + e.getMessage());
        }
    }

    private void navigateToDocsActivity() {
        Log.d(TAG, "MainFragment에서 문서 선택하여 DocsActivity 실행 시작");
        try {
            Thread.sleep(3000);
            onView(withId(mainFragmentDocsRecyclerViewId))
                    .check(matches(isDisplayed()))
                    .perform(actionOnItemAtPosition(0, click()));
            intended(hasComponent(DocsActivity.class.getName()));
            Thread.sleep(8000);
        } catch (Exception e) {
            fail("DocsActivity로 이동 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testDocsDetailAdapter_ItemContentDisplay() {
        Log.d(TAG, "testDocsDetailAdapter_ItemContentDisplay: 테스트 시작");
        performLoginIfNeeded();
        navigateToDocsActivity();

        Log.d(TAG, "  RecyclerView 및 첫 번째 아이템의 제목, 시간, 본문 표시 여부 및 내용 비어있지 않음 확인");
        onView(withId(docsDetailItemRecyclerViewId))
                .check(matches(isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(allOf(withId(itemTitleId), isDisplayed(), not(withText("")))))))
                .check(matches(atPosition(0, hasDescendant(allOf(withId(itemTimeId), isDisplayed(), not(withText("")))))))
                .check(matches(atPosition(0, hasDescendant(allOf(withId(itemDescriptionId), isDisplayed(), not(withText("")))))));
        Log.d(TAG, "  아이템 내용 표시 확인 완료");
    }

    @Test
    public void testTextSelectionAndSearchFromPopup() {
        Log.d(TAG, "testTextSelectionAndSearchFromPopup: 테스트 시작");
        performLoginIfNeeded();
        navigateToDocsActivity();

        Log.d(TAG, "  본문 텍스트 길게 클릭하여 팝업 메뉴 테스트 시작");
        try {
            onView(withId(docsDetailItemRecyclerViewId))
                    .perform(actionOnItemAtPosition(0, actionOnChild(itemDescriptionId, longClick())));
            Log.d(TAG, "    첫 번째 아이템의 본문 텍스트 길게 클릭 완료.");
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("본문 텍스트 길게 클릭 중 예외 발생: " + e.getMessage());
        }

        Log.d(TAG, "    팝업 메뉴에서 '검색하기' 클릭 시도");
        String searchMenuText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.keyword_title);
        onView(withText(searchMenuText)).perform(click());

        Log.d(TAG, "    KeywordDetailActivity 실행 및 extra 검증");
        intended(allOf(
                hasComponent(KeywordDetailActivity.class.getName()),
                hasExtra("keyword", not(emptyOrNullString()))
        ));
        Log.d(TAG, "    KeywordDetailActivity 이동 확인 완료.");
    }

    @Test
    public void testCreateCommentOnNewHighlight() {
        Log.d(TAG, "testCreateCommentOnNewHighlight: 테스트 시작");

        performLoginIfNeeded();
        navigateToDocsActivity();

        Log.d(TAG, "  본문 텍스트 길게 클릭하여 새 하이라이트 및 댓글 생성 시작");
        try {
            onView(withId(docsDetailItemRecyclerViewId))
                    .perform(actionOnItemAtPosition(0, actionOnChild(itemDescriptionId, longClick())));
            Log.d(TAG, "    첫 번째 아이템의 본문 텍스트 길게 클릭 완료.");
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("본문 텍스트 길게 클릭 중 예외 발생: " + e.getMessage());
        }

        Log.d(TAG, "    팝업 메뉴에서 '댓글 달기' 클릭 시도");
        // ============================ 수정된 부분 ============================
        // 팝업 메뉴의 실제 텍스트 리소스를 사용해야 합니다.
        // 예를 들어, menu.xml에서 android:title="@string/action_comment" 로 정의되었다고 가정합니다.
        String addCommentMenuText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.dialog_comment_hint);
        // ===============================================================
        onView(withText(addCommentMenuText)).perform(click());

        Log.d(TAG, "  댓글 BottomSheetDialog 표시 확인");
        try {
            Thread.sleep(1500); // BottomSheetDialog가 완전히 올라올 시간 대기
            // RecyclerView는 초기에 비어있을 수 있으므로 isDisplayed() 검증에서 제외하고,
            // 항상 보이는 다른 뷰들을 검증합니다.
            onView(withId(commentDialogInputEditTextId)).check(matches(isDisplayed()));
            onView(withId(commentDialogConfirmButtonId)).check(matches(isDisplayed()));
            onView(withId(commentDialogTitleId)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("댓글 다이얼로그 UI 검증 중 예외 발생: " + e.getMessage());
        }

        String newCommentText = "Espresso 새 댓글 " + System.currentTimeMillis() % 1000;
        Log.d(TAG, "  새 댓글 작성 및 전송: " + newCommentText);
        onView(withId(commentDialogInputEditTextId)).perform(replaceText(newCommentText), closeSoftKeyboard());
        onView(withId(commentDialogConfirmButtonId)).perform(click());

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // UI 반영 대기

        Log.d(TAG, "  새 댓글이 목록에 추가되었는지 확인");
        // 이제 댓글이 추가되어 RecyclerView가 표시되고 내용을 가짐
        onView(withId(commentDialogRecyclerViewId))
                .check(matches(isDisplayed())) // 이제는 보여야 함
                .check(matches(atPosition(0, hasDescendant(allOf(withId(itemCommentContentId), withText(newCommentText))))));

        Log.d(TAG, "testCreateCommentOnNewHighlight: 테스트 완료");
    }

    private void verifySharedPreferencesAfterLogin() {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        assertTrue("로그인 성공 후 Access token은 null이 아니어야 합니다.", !TextUtils.isEmpty(accessToken));
    }

    public static ViewAction actionOnChild(final int childViewId, final ViewAction viewAction) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(View.class);
            }
            @Override
            public String getDescription() {
                return "Perform action on a child view with specified id.";
            }
            @Override
            public void perform(UiController uiController, View view) {
                View childView = view.findViewById(childViewId);
                if (childView != null) {
                    viewAction.perform(uiController, childView);
                } else {
                    fail("Child view with id " + childViewId + " not found inside RecyclerView item.");
                }
            }
        };
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