package team.y2k2.globa.docs.quiz.quizconduct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.databinding.ActivityQuizBinding;
import team.y2k2.globa.docs.quiz.quizresult.QuizResultActivity;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    int folderId, recordId;
    QuizViewModel quizViewModel;
    List<Quiz> quizList;
    private int currentIndex = 0;
    private List<Boolean> answerList = new ArrayList<>();
    private List<QuizResult> quizResultList = new ArrayList<>();
    boolean isReceived = false;
    private MutableLiveData<Boolean> isReceivedLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonQuizBack.setOnClickListener(v -> {
            finish();
        });

        folderId = Integer.parseInt(getIntent().getStringExtra("folderId"));
        recordId = Integer.parseInt(getIntent().getStringExtra("recordId"));

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.gatherQuiz(folderId, recordId);

        quizViewModel.getQuizLiveData().observe(this, quiz -> {
            if(quiz != null) {
                quizList = quiz;
                Log.d("api 수신 여부", "수신 성공");
                isReceivedLiveData.postValue(true);

            } else {
                Log.d("api 수신 여부", "수신 실패");
            }
        });

        isReceivedLiveData.observe(this, quizReceived -> {
            Log.d("퀴즈 시작", "퀴즈 시작");
            // 1번 문제 설정
            binding.textviewQuizCount.setText("문제 1/" + quizList.size());
            binding.textviewQuizQuestion.setText(quizList.get(currentIndex).getQuestion());
            Log.d("퀴즈 정답", String.valueOf(quizList.get(currentIndex).getAnswer()));
        });

        // O 선택
        binding.layoutQuizCorrect.setOnClickListener(v -> {
            fetchQuiz(1);
        });

        // X 선택
        binding.layoutQuizWrong.setOnClickListener(v -> {
            fetchQuiz(0);
        });

    }

    protected void fetchQuiz(int answer) {

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
            binding.textviewQuizCount.setText("문제 " + (currentIndex + 1) + "/" + quizList.size());
            binding.textviewQuizQuestion.setText(quizList.get(currentIndex).getQuestion());
            Log.d("퀴즈 정답", String.valueOf(quizList.get(currentIndex).getAnswer()));
        } else {
            // 결과화면으로 넘어가기전 퀴즈 결과 api 전송
            quizViewModel.submitQuizResult(folderId, recordId, quizResultList);

            // 결과화면으로 넘어가기전 결과 계산
            int totalQuestion = quizList.size(); // 총 문제 수
            Log.d("총 문제 수", String.valueOf(totalQuestion));
            int correctAnswer = countTrue(answerList); // 총 정답 수
            Log.d("총 정답 수", String.valueOf(correctAnswer));
            int grade = (int) (((double)correctAnswer / (double)totalQuestion) * 100); // 최종 점수
            Log.d("최종 점수", String.valueOf(grade));
            Log.d("퀴즈 정보", "문제 수 : " + totalQuestion + ", 정답 수 : " + correctAnswer + ", 점수 : " + grade);

            // 결과화면으로 이동
            Intent intent = new Intent(this, QuizResultActivity.class);
            intent.putExtra("grade", grade); // 최종 점수 전송
            intent.putExtra("correctAnswer", correctAnswer); // 총 정답 수 전송
            startActivity(intent);
            finish();
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