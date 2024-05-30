package team.y2k2.globa.docs.quiz.quizconduct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.databinding.ActivityQuizBinding;
import team.y2k2.globa.docs.quiz.quizresult.QuizResultActivity;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    int folderId;

    QuizViewModel quizViewModel;
    List<Quiz> quizList;
    private int currentIndex = 0;
    private List<Boolean> answerList;
    private List<QuizResult> quizResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonQuizBack.setOnClickListener(v -> {
            finish();
        });

        SharedPreferences folerPreferences = getSharedPreferences("folderId", MODE_PRIVATE);
        folderId = folerPreferences.getInt("folderId", 0);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.gatherQuiz(folderId, 0);

        quizViewModel.getQuizLiveData().observe(this, quiz -> {
            if(quiz != null) {
                quizList = quiz.getQuizs();
            }
        });

        // 1번 문제 설정
        if(!quizList.isEmpty()) {
            binding.textviewQuizCount.setText("문제 1/10");
            binding.textviewQuizQuestion.setText(quizList.get(currentIndex).getQuestion());
        }

        // O 선택
        binding.layoutQuizCorrect.setOnClickListener(v -> {
            fetchQuiz(true);
        });

        // X 선택
        binding.layoutQuizWrong.setOnClickListener(v -> {
            fetchQuiz(false);
        });

    }

    protected void fetchQuiz(boolean answer) {

        if(quizList.get(currentIndex).getAnswer() == answer) {
            // 정답일 때
            answerList.add(true);
            quizResultList.add(new QuizResult(quizList.get(currentIndex).getQuizId(), true));
        } else {
            // 오답일 때
            answerList.add(false);
            quizResultList.add(new QuizResult(quizList.get(currentIndex).getQuizId(), false));
        }
        currentIndex++;
        if(currentIndex < quizList.size()) {
            binding.textviewQuizCount.setText("문제 " + (currentIndex + 1) + "/10");
            binding.textviewQuizQuestion.setText(quizList.get(currentIndex).getQuestion());
        } else {
            // 결과화면으로 넘어가기전 퀴즈 결과 api 전송
            quizViewModel.submitQuizResult(folderId, 0, quizResultList);

            // 결과화면으로 넘어가기전 결과 계산
            int totalQuestion = quizList.size(); // 총 문제 수
            int correctAnswer = countTrue(answerList); // 총 정답 수
            int grade = (int) (correctAnswer / totalQuestion * 100); // 최종 점수


            // 결과화면으로 이동
            Intent intent = new Intent(this, QuizResultActivity.class);
            intent.putExtra("grade", grade); // 최종 점수 전송
            intent.putExtra("correctAnswer", correctAnswer); // 총 정답 수 전송
            startActivity(intent);
        }

    }

    protected static int countTrue(List<Boolean> answerList) {
        int count = 0;
        for(Boolean answer : answerList) {
            if (answer) {
                count++;
            }
        }
        return count;
    }

}