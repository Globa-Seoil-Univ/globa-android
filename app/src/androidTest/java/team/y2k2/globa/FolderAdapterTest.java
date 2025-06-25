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
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail; // fail 추가

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager; // ToastMatcher를 위해 추가 (만약 사용한다면)
import android.os.IBinder;      // ToastMatcher를 위해 추가 (만약 사용한다면)


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException; // NoMatchingViewException import
import androidx.test.espresso.Root;
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
import team.y2k2.globa.main.folder.add.FolderAddActivity;
// import team.y2k2.globa.main.folder.inside.FolderInsideFragment; // 현재 테스트에서 직접 사용 안 함
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class FolderAdapterTest {

    private static final String TAG = "FolderAdapterTest";

    // MainActivity UI 요소 ID
    private static final int mainActivityFolderTabId = R.id.item_main_folder;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main; // MainActivity의 FragmentContainerView ID
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top; // MainActivity의 홈 화면(MainFragment) 확인용

    // FolderFragment UI 요소 ID
    private static final int folderFragmentRecyclerViewId = R.id.recyclerview_folder;
    private static final int folderFragmentAddButtonId = R.id.image_button_folder_add;

    // FolderAdapter 아이템 (item_folder.xml) 내부 View ID
    private static final int itemFolderLayoutId = R.id.constraintlayout_folder_item;
    private static final int itemFolderTitleId = R.id.textview_folder_item_title;
    // private static final int itemFolderDatetimeId = R.id.textview_folder_item_datetime; // 현재 테스트에서 직접 사용 안함

    // FolderInsideFragment 내 고유 ID (전환 확인용 - 현재 테스트에서는 직접적인 사용은 없으나, 폴더 클릭 후 화면 전환 검증에 사용될 수 있음)
    // private static final int folderInsideFragmentUniqueViewId = R.id.recyclerview_folder_inside_docs;

    // FolderAddActivity 내 View ID
    private static final int folderAddActivityNameEditTextId = R.id.edittext_folder_add_input_name;
    private static final int folderAddActivityConfirmButtonId = R.id.textview_folder_add_confirm;

    // AlertDialog 텍스트
    private static final String ALERT_DIALOG_TITLE_FOLDER_DELETE = "폴더 삭제"; // 실제 strings.xml 리소스 사용 권장
    private static final String ALERT_DIALOG_BUTTON_YES = "예";           // 실제 strings.xml 리소스 사용 권장
    // private static final String ALERT_DIALOG_BUTTON_NO = "아니오";      // 필요시 사용

    private final String NEW_FOLDER_NAME = "자동 생성 테스트 폴더 " + System.currentTimeMillis() % 10000;

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
    public void testFolderAdd_Then_Delete_AfterFullFlow() {
        Log.d(TAG, "testFolderAdd_Then_Delete_AfterFullFlow: 테스트 시작");

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

            onView(withId(mainActivityHomeIndicatorId)).check(matches(isDisplayed())); // MainActivity 홈 상태 확인
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

        // === 2단계: MainActivity에서 폴더 탭으로 이동 ===
        Log.d(TAG, "  2단계: 폴더 탭으로 이동 시작");
        onView(withId(mainActivityFolderTabId)).perform(click());
        try {
            Thread.sleep(3000); // FolderFragment의 API 호출 및 UI 로드 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (FolderFragment 로딩 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }
        onView(withId(folderFragmentRecyclerViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  2단계: 폴더 탭으로 이동 및 폴더 목록 표시 확인 완료");

        // === 3단계: 폴더 추가 테스트 ===
        Log.d(TAG, "  3단계: 폴더 추가 테스트 시작");
        onView(withId(folderFragmentAddButtonId)).perform(click());
        intended(hasComponent(FolderAddActivity.class.getName()));

        Log.d(TAG, "    FolderAddActivity에서 새 폴더 이름 입력: " + NEW_FOLDER_NAME);
        try {
            Thread.sleep(1000);
            onView(withId(folderAddActivityNameEditTextId))
                    .perform(replaceText(NEW_FOLDER_NAME), closeSoftKeyboard());
            onView(withId(folderAddActivityConfirmButtonId))
                    .perform(click());
        } catch (InterruptedException e) {
            Log.e(TAG, "    FolderAddActivity와 상호작용 중 Thread.sleep 중단됨: " + e.getMessage());
            Thread.currentThread().interrupt(); return;
        } catch (Exception e) {
            Log.e(TAG, "    FolderAddActivity와 상호작용 중 오류 발생: " + e.getMessage());
            throw e;
        }

        Log.d(TAG, "    FolderFragment로 돌아와 목록 갱신 대기 중...");
        try {
            Thread.sleep(3000); // API 호출 및 UI 갱신 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (폴더 추가 후 목록 갱신 대기 중)", e);
            Thread.currentThread().interrupt(); return;
        }

        Log.d(TAG, "    추가된 폴더 '" + NEW_FOLDER_NAME + "'가 목록에 보이는지 확인 중...");
        onView(withId(folderFragmentRecyclerViewId))
                .perform(scrollTo(hasDescendant(withText(NEW_FOLDER_NAME))))
                .check(matches(hasDescendant(withText(NEW_FOLDER_NAME))));
        Log.d(TAG, "  3단계: 폴더 추가 및 목록에서 확인 완료");

        // === 4단계: 추가된 폴더 삭제 테스트 ===
        Log.d(TAG, "  4단계: 추가된 폴더 삭제 테스트 시작 (" + NEW_FOLDER_NAME + ")");
        onView(allOf(withId(itemFolderLayoutId), hasDescendant(withText(NEW_FOLDER_NAME))))
                .perform(longClick());

        // AlertDialog의 텍스트는 실제 앱의 문자열 리소스 또는 정의된 상수를 사용하는 것이 좋습니다.
        onView(withText(ALERT_DIALOG_TITLE_FOLDER_DELETE)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(ALERT_DIALOG_BUTTON_YES)).inRoot(isDialog()).perform(click());

        // Toast 메시지 확인은 현재 주석 처리된 상태 유지 (필요 시 ToastMatcher 구현 및 사용)
        Log.d(TAG, "    폴더 삭제 API 호출 및 UI 갱신 대기 중...");
        try {
            Thread.sleep(3000); // API 호출 및 UI 갱신 대기 (IdlingResource 권장)
        } catch (InterruptedException e) {
            Log.e(TAG, "    Thread.sleep 중단됨 (폴더 삭제 후 목록 갱신 대기 중)", e);
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "    삭제된 폴더 '" + NEW_FOLDER_NAME + "'가 목록에서 사라졌는지 확인 중...");
        onView(withId(folderFragmentRecyclerViewId))
                .check(matches(not(hasDescendant(withText(NEW_FOLDER_NAME)))));
        Log.d(TAG, "  4단계: 추가된 폴더 삭제 및 목록에서 사라짐 확인 완료");

        Log.d(TAG, "testFolderAdd_Then_Delete_AfterFullFlow: 모든 테스트 완료");
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

    // RecyclerView의 특정 위치의 아이템이 특정 자식 Matcher를 만족하는지 확인하는 Matcher
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

    // ToastMatcher는 현재 테스트에서 사용되지 않으므로 주석 처리 또는 삭제 가능
    /*
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }
        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST || type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
                View windowDecorView = root.getDecorView();
                if (windowDecorView != null) {
                    // 실제 Toast 윈도우는 application window token과 동일한 window token을 가집니다. (이 부분은 상황에 따라 다를 수 있음)
                    // 좀 더 정확한 방법은 windowToken != appToken
                    return windowDecorView.getWindowToken() == windowDecorView.getApplicationWindowToken();
                }
            }
            return false;
        }
    }
    */
}