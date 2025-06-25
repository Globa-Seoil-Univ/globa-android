package team.y2k2.globa;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
// import static androidx.test.espresso.matcher.ViewMatchers.withText; // 필요시 사용
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

// androidx.recyclerview.widget.RecyclerView는 직접적인 타입 참조가 없으면 제거 가능
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
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
import team.y2k2.globa.docs.quiz.conduct.QuizActivity;
import team.y2k2.globa.docs.quiz.result.QuizResultActivity;
import team.y2k2.globa.intro.IntroActivity;
import team.y2k2.globa.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class QuizActivityTest {

    private static final String TAG = "QuizActivityTest";

    // MainActivity (MainFragment)
    private static final int mainActivityHomeIndicatorId = R.id.imageview_main_top;
    private static final int mainFragmentDocumentRecyclerViewId = R.id.recyclerview_main_document;
    private static final int mainActivityFragmentContainerId = R.id.fragment_container_view_main; // MainActivity의 FragmentContainerView ID

    // DocsActivity
    private static final int docsActivityLayoutId = R.id.constraintlayout_docs;
    private static final int docsActivityMoreButtonId = R.id.imageview_docs_more;

    // DocsMoreActivity
    private static final int docsMoreActivityQuizLayoutId = R.id.relativelayout_docs_more_quiz;
    // private static final int docsMoreActivityLayoutId = R.id.activity_docs_more; // 실제 레이아웃 파일의 루트 ID로 변경해야 할 수 있음


    // QuizActivity
    private static final int quizActivityBackButtonId = R.id.button_quiz_back;
    private static final int quizActivityCountTextViewId = R.id.textview_quiz_count;
    private static final int quizActivityQuestionTextViewId = R.id.textview_quiz_question;
    private static final int quizActivityCorrectLayoutId = R.id.layout_quiz_correct;

    // QuizResultActivity
    private static final int quizResultActivityScoreTextViewId = R.id.textview_quiz_result_score;
    private static final int quizResultActivityCorrectCountTextViewId = R.id.textview_quiz_result_correct;

    @Rule
    public ActivityScenarioRule<IntroActivity> activityRule = new ActivityScenarioRule<>(IntroActivity.class);

    private int docsActivityLaunchAttempt;
    private int docsMoreActivityLaunchAttempt;
    private int quizActivityLaunchAttempt;

    @Before
    public void setUp() {
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        Log.d(TAG, "setUp: 테스트 준비 완료");
        docsActivityLaunchAttempt = 0;
        docsMoreActivityLaunchAttempt = 0;
        quizActivityLaunchAttempt = 0;
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        Intents.release();
        Log.d(TAG, "tearDown: 테스트 환경 정리 완료");
    }

    private void performLoginAndNavigateToMain() {
        Log.d(TAG, "performLoginAndNavigateToMain: 시작");
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
            verifySharedPreferencesAfterLogin();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Login or navigation to Main interrupted: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Login or navigation to Main 중 예외 발생", e);
            fail("Login or navigation to Main 중 예외 발생: " + e.getMessage());
        }
        Log.d(TAG, "  로그인 및 MainActivity(MainFragment 활성화) 진입 완료 (또는 이미 진입됨)");
    }

    private void navigateToQuizActivityFromMain() {
        docsActivityLaunchAttempt++;
        docsMoreActivityLaunchAttempt++;
        quizActivityLaunchAttempt++;

        Log.d(TAG, "    QuizActivity로 이동 중... (Docs: " + docsActivityLaunchAttempt +
                ", More: " + docsMoreActivityLaunchAttempt + ", Quiz: " + quizActivityLaunchAttempt + ")");
        try {
            Log.d(TAG, "    MainFragment에서 첫 번째 문서 아이템 클릭 시도...");
            onView(withId(mainFragmentDocumentRecyclerViewId)).check(matches(isDisplayed()));
            onView(withId(mainFragmentDocumentRecyclerViewId)).perform(actionOnItemAtPosition(0, click()));

            intended(allOf(
                    hasComponent(DocsActivity.class.getName()),
                    hasExtra("folderId", not(emptyOrNullString())),
                    hasExtra("recordId", not(emptyOrNullString()))
            ), Intents.times(docsActivityLaunchAttempt));
            Log.d(TAG, "    DocsActivity(" + docsActivityLaunchAttempt + ") 진입 확인.");
            Thread.sleep(3000);

            Log.d(TAG, "    DocsActivity에서 '더보기' 버튼 클릭 시도...");
            onView(withId(docsActivityMoreButtonId)).check(matches(isDisplayed()));
            onView(withId(docsActivityMoreButtonId)).perform(click());

            intended(allOf(
                    hasComponent(DocsMoreActivity.class.getName()),
                    hasExtra("folderId", not(emptyOrNullString())),
                    hasExtra("recordId", not(emptyOrNullString())),
                    hasExtra("title", not(emptyOrNullString())),
                    hasExtra("folderTitle", not(emptyOrNullString()))
            ), Intents.times(docsMoreActivityLaunchAttempt));
            Log.d(TAG, "    DocsMoreActivity(" + docsMoreActivityLaunchAttempt + ") 진입 확인.");
            Thread.sleep(2000);

            Log.d(TAG, "    DocsMoreActivity에서 '퀴즈 풀기' 클릭 시도...");
            onView(withId(docsMoreActivityQuizLayoutId)).check(matches(isDisplayed()));
            onView(withId(docsMoreActivityQuizLayoutId)).perform(click());

            intended(allOf(
                    hasComponent(QuizActivity.class.getName()),
                    hasExtra("folderId", not(emptyOrNullString())),
                    hasExtra("recordId", not(emptyOrNullString()))
            ), Intents.times(quizActivityLaunchAttempt));
            Log.d(TAG, "    QuizActivity(" + quizActivityLaunchAttempt + ") 진입 확인.");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Navigation to QuizActivity interrupted: " + e.getMessage());
        }
        Log.d(TAG, "    QuizActivity 진입 완료.");
    }

    @Test
    public void testQuizFlow_AnswerAllAndNavigateToResult() {
        Log.d(TAG, "testQuizFlow_AnswerAllAndNavigateToResult: 테스트 시작");
        performLoginAndNavigateToMain(); // 조건부 로그인 로직이 포함된 메소드 사용
        navigateToQuizActivityFromMain();

        Log.d(TAG, "  퀴즈 진행 시작");
        onView(withId(quizActivityCountTextViewId)).check(matches(isDisplayed()));
        onView(withId(quizActivityQuestionTextViewId)).check(matches(isDisplayed()));
        onView(withId(quizActivityCorrectLayoutId)).check(matches(isDisplayed()));

        int maxQuestionsToAnswer = 15;
        boolean quizResultActivityLaunched = false;
        for (int i = 0; i < maxQuestionsToAnswer; i++) {
            try {
                onView(withId(quizActivityQuestionTextViewId)).check(matches(isDisplayed()));
                Log.d(TAG, "  질문 " + (i + 1) + "에 'O' 답변 중...");
                onView(withId(quizActivityCorrectLayoutId)).perform(click());
                Thread.sleep(1000);
            } catch (NoMatchingViewException e) {
                Log.d(TAG, "  질문 View를 찾을 수 없음. 퀴즈 종료 및 결과 화면으로 전환된 것으로 추정.");
                quizResultActivityLaunched = true;
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Quiz answering interrupted: " + e.getMessage());
            }
        }

        assertTrue("퀴즈가 " + maxQuestionsToAnswer + "번의 시도 후에도 결과 화면으로 넘어가지 못했습니다.", quizResultActivityLaunched);

        Log.d(TAG, "  퀴즈 결과 화면(QuizResultActivity) 실행 및 extra 검증");
        intended(allOf(
                hasComponent(QuizResultActivity.class.getName()),
                hasExtra("grade", instanceOf(Integer.class)),
                hasExtra("correctAnswer", instanceOf(Integer.class))
        ), Intents.times(1));

        onView(withId(quizResultActivityScoreTextViewId)).check(matches(isDisplayed()));
        onView(withId(quizResultActivityCorrectCountTextViewId)).check(matches(isDisplayed()));
        Log.d(TAG, "  QuizResultActivity UI 일부 검증 완료.");
        Log.d(TAG, "testQuizFlow_AnswerAllAndNavigateToResult: 테스트 완료");
    }

    @Test
    public void testQuizActivity_BackButtonFunctionality() {
        Log.d(TAG, "testQuizActivity_BackButtonFunctionality: 테스트 시작");
        performLoginAndNavigateToMain(); // 조건부 로그인 로직이 포함된 메소드 사용
        navigateToQuizActivityFromMain();

        Log.d(TAG, "  퀴즈 한 문제 답변 후 뒤로가기 시도");
        try {
            onView(withId(quizActivityQuestionTextViewId)).check(matches(isDisplayed()));
            onView(withId(quizActivityCorrectLayoutId)).perform(click());
            Thread.sleep(500);
        } catch (NoMatchingViewException e) {
            Log.w(TAG, "  퀴즈 첫 문제 로드 실패. 뒤로가기 버튼 테스트의 정확도에 영향 가능성.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Quiz answering for back button test interrupted: " + e.getMessage());
        }

        onView(withId(quizActivityBackButtonId)).perform(click());
        Log.d(TAG, "  QuizActivity 뒤로가기 버튼 클릭됨.");
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // DocsMoreActivity의 특정 View(예: DocsMoreActivity의 루트 레이아웃)가 보이는지 확인
        onView(withId(R.id.activity_docs_more)).check(matches(isDisplayed())); // DocsMoreActivity의 루트 레이아웃 ID로 변경 (예: R.id.activity_docs_more)
        Log.d(TAG, "  뒤로가기 후 DocsMoreActivity 화면으로 복귀 확인.");

        Log.d(TAG, "testQuizActivity_BackButtonFunctionality: 테스트 완료");
    }

    private void verifySharedPreferencesAfterLogin() {
        SharedPreferences prefs = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);
        assertTrue("로그인 성공 후 Access token은 null이 아니어야 합니다.", !TextUtils.isEmpty(accessToken));
        // 나머지 SharedPreferences 값들도 필요에 따라 검증
        Log.d(TAG, "    SharedPreferences 검증 완료.");
    }
}