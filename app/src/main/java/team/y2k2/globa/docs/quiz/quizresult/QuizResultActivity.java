package team.y2k2.globa.docs.quiz.quizresult;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Map;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityQuizResultBinding;

public class QuizResultActivity extends AppCompatActivity {

    private ActivityQuizResultBinding binding;
    private SharedPreferences quizResultPref;
    private SharedPreferences.Editor quizResultEditor;

    private int gradeInt;
    private int correctedInt;

    private String grade;
    private String corrected;
    private String percentString;
    private Map<String, ?> allEntries;

    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        color = ContextCompat.getColor(this, R.color.primary);

        initializeUI();

    }

    private void initializeUI() {

        binding.buttonQuizresultBack.setOnClickListener(v -> {
            finish();
        });

        // 데이터 수집
        loadData();

        // 정답 표시
        setScoreText();

        // 점수 표시
        setCorrectedText();

        // 상승률 표시
        setIncreasedText();

    }

    private void loadData() {
        // 띄어줄 점수와 정답 수
        gradeInt = getIntent().getIntExtra("grade", 0);
        correctedInt = getIntent().getIntExtra("correctAnswer", 0);
        grade = gradeInt + getString(R.string.score);
        corrected = correctedInt + getString(R.string.count);
    }

    private void setScoreText() {
        // 점수
        SpannableStringBuilder scoreSpannable = new SpannableStringBuilder(grade);
        int scoreStartIndex = 0;
        int scoreEndIndex = grade.length() - 1;
        scoreSpannable.setSpan(new ForegroundColorSpan(color), scoreStartIndex, scoreEndIndex, 0);
        binding.textviewQuizresultScore.setText(scoreSpannable);
    }

    private void setCorrectedText() {
        // 정답 수
        SpannableStringBuilder countSpannable = new SpannableStringBuilder(corrected);
        int countStartIndex = 0;
        int countEndIndex = corrected.length() - 1;
        countSpannable.setSpan(new ForegroundColorSpan(color), countStartIndex, countEndIndex, 0);
        binding.textviewQuizresultCorrect.setText(countSpannable);
    }

    private void setIncreasedText() {

        loadPreference();

        if(allEntries.isEmpty()) {
            // 문제 푼 이력이 없는 경우
            percentString = getString(R.string.activity_quiz_result_empty);
            SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
            percentSpannable.setSpan(new ForegroundColorSpan(color), 9, 12, 0); // "상승률" 글자에만 색 입히기
            binding.textviewQuizresultPercent.setText(percentSpannable);
            quizResultEditor.putInt("lastGrade", gradeInt);
            quizResultEditor.apply();
        } else {
            // 문제 푼 이력이 있는 경우
            int lastCorrected = quizResultPref.getInt("lastGrade", 0);
            int different;
            if(lastCorrected != 0) {
                if(gradeInt > lastCorrected) {
                    // 점수 상승 경우
                    different = gradeInt - lastCorrected;
                    percentString = getString(R.string.activity_quiz_result_last_corrected_1) + " " + different + getString(R.string.activity_quiz_result_last_corrected_2);
                    SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
                    int percentStartIndex = 9;
                    int percentEndIndex = percentString.length() - 1;
                    percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex - 8, 0);
                    binding.textviewQuizresultPercent.setText(percentSpannable);
                } else if (gradeInt < lastCorrected) {
                    // 점수 하락 경우
                    different = lastCorrected - gradeInt;
                    percentString = getString(R.string.activity_quiz_result_last_corrected_1) + " " + different + getString(R.string.activity_quiz_result_last_corrected_2);
                    SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
                    int percentStartIndex = 9;
                    int percentEndIndex = percentString.length() - 1;
                    percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex - 8, 0);
                    binding.textviewQuizresultPercent.setText(percentSpannable);
                } else {
                    // 점수 동일 경우
                    percentString = getString(R.string.activity_quiz_result_last_corrected_4);
                    binding.textviewQuizresultPercent.setText(percentString);
                }
            } else {
                // 지난 성적이 0점인 경우
                percentString = getString(R.string.activity_quiz_result_last_score_zero);
                binding.textviewQuizresultPercent.setText(percentString);
            }
            quizResultEditor.putInt("lastGrade", gradeInt);
            quizResultEditor.apply();
        }
    }

    private void loadPreference() {
        quizResultPref = getSharedPreferences("quizResult", MODE_PRIVATE);
        quizResultEditor = quizResultPref.edit();
        allEntries = quizResultPref.getAll();
    }

}