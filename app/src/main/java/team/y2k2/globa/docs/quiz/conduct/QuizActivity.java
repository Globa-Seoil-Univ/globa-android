package team.y2k2.globa.docs.quiz.conduct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.databinding.ActivityQuizBinding;
import team.y2k2.globa.docs.quiz.result.QuizResultActivity;

public class QuizActivity extends AppCompatActivity {
    private final List<Boolean> answerList = new ArrayList<>();
    private final List<QuizResult> quizResultList = new ArrayList<>();
    private ActivityQuizBinding binding;
    private int folderId, recordId;
    private QuizActivityModel quizActivityModel;
    private List<Quiz> quizList;
    private int currentIndex = 0;

    protected static int countTrue(List<Boolean> answerList) {
        int count = 0;
        for (Boolean answer : answerList) {
            if (answer) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quizActivityModel = new ViewModelProvider(this).get(QuizActivityModel.class);
        quizActivityModel.setContext(this);
        quizActivityModel.initialize();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.layoutQuiz.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.layoutQuiz.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            binding.layoutQuiz.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });

        initializeUI();
        observeViewModel();
        loadData();
    }

    private void initializeUI() {
        binding.buttonQuizBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.layoutQuizCorrect.setOnClickListener(v -> fetchQuiz(true));  // O 버튼은 true
        binding.layoutQuizWrong.setOnClickListener(v -> fetchQuiz(false)); // X 버튼은 false
    }

    private void observeViewModel() {
        quizActivityModel.getQuizLiveData().observe(this, quizzes -> {
            if (quizzes != null && !quizzes.isEmpty()) {
                this.quizList = quizzes;
                Log.d("QuizActivity", "퀴즈 데이터 수신 성공. 총 " + quizList.size() + "개의 퀴즈");
                startQuiz();
            } else {
                Log.e("QuizActivity", "퀴즈 데이터 수신에 실패했거나 퀴즈가 없습니다.");
                finish();
            }
        });
    }

    private void loadData() {
        try {
            folderId = Integer.parseInt(getIntent().getStringExtra("folderId"));
            recordId = Integer.parseInt(getIntent().getStringExtra("recordId"));
            quizActivityModel.gatherQuiz(folderId, recordId);
        } catch (NumberFormatException e) {
            Log.e("QuizActivity", "folderId 또는 recordId 파싱 실패", e);
            finish();
        }
    }

    private void startQuiz() {
        currentIndex = 0;
        updateQuestionUI();
    }

    private void updateQuestionUI() {
        if (quizList == null || currentIndex >= quizList.size()) return;

        binding.textviewQuizCount.setText(getString(R.string.quiz) + " " + (currentIndex + 1) + "/" + quizList.size());
        binding.textviewQuizQuestion.setText(quizList.get(currentIndex).getQuestion());
        Log.d("QuizActivity", "현재 문제: " + (currentIndex + 1) + ", 정답: " + quizList.get(currentIndex).getAnswer());
    }


    protected void fetchQuiz(boolean userAnswer) {
        if (quizList == null || currentIndex >= quizList.size()) {
            Log.e("QuizActivity", "퀴즈 목록이 없거나 인덱스를 초과하여 처리할 수 없습니다.");
            return;
        }

        boolean correctAnswer = quizList.get(currentIndex).getAnswer();
        int currentQuizId = quizList.get(currentIndex).getQuizId();

        if (userAnswer == correctAnswer) {
            answerList.add(true);
            quizResultList.add(new QuizResult(currentQuizId, true));
        } else {
            answerList.add(false);
            quizResultList.add(new QuizResult(currentQuizId, false));
        }

        currentIndex++;

        if (currentIndex < quizList.size()) {
            updateQuestionUI();
        } else {
            submitAndShowResult();
        }
    }

    private void submitAndShowResult() {
        // 1. 퀴즈 결과 API 전송
        quizActivityModel.submitQuizResult(folderId, recordId, quizResultList);

        // 2. 결과 계산
        int totalQuestion = quizList.size();
        int correctAnswerCount = countTrue(answerList);
        int grade = (int) (((double) correctAnswerCount / (double) totalQuestion) * 100);

        // 3. 결과 화면으로 이동
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("grade", grade);
        intent.putExtra("correctAnswer", correctAnswerCount);
        startActivity(intent);
        finish();
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("퀴즈 중단")
                .setMessage("퀴즈를 중단하고 나가시겠습니까? 진행 상황은 저장되지 않습니다.")
                .setPositiveButton("나가기", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("취소", null) // "취소"를 누르면 다이얼로그만 닫힙니다.
                .show();
    }
}