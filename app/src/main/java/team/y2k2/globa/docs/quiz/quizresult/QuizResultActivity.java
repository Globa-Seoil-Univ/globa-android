package team.y2k2.globa.docs.quiz.quizresult;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Map;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityQuizResultBinding;

public class QuizResultActivity extends AppCompatActivity {

    private ActivityQuizResultBinding binding;
    private SharedPreferences quizResultPref;
    private SharedPreferences.Editor quizResultEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lotti 재생
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(binding.imageviewQuizresultClear);
        Glide.with(this).load(R.drawable.clear).into(gifImage);

        binding.buttonQuizresultBack.setOnClickListener(v -> {
            finish();
        });

        // 띄어줄 점수와 정답 수
        int gradeInt = getIntent().getIntExtra("grade", 0);
        int correctedInt = getIntent().getIntExtra("correctAnswer", 0);
        String grade = String.valueOf(gradeInt) + "점";
        String corrected = String.valueOf(correctedInt) + "개";

        int color = getResources().getColor(R.color.primary);
        // 점수
        SpannableStringBuilder scoreSpannable = new SpannableStringBuilder(grade);
        int scoreStartIndex = 0;
        int scoreEndIndex = grade.length() - 1;
        scoreSpannable.setSpan(new ForegroundColorSpan(color), scoreStartIndex, scoreEndIndex - 1, 0);
        binding.textviewQuizresultScore.setText(scoreSpannable);

        // 정답 수
        SpannableStringBuilder countSpannable = new SpannableStringBuilder(corrected);
        int countStartIndex = 0;
        int countEndIndex = corrected.length() - 1;
        countSpannable.setSpan(new ForegroundColorSpan(color), countStartIndex, countEndIndex - 1, 0);
        binding.textviewQuizresultCorrect.setText(countSpannable);

        // 상승률
        quizResultPref = getSharedPreferences("quizResult", MODE_PRIVATE);
        quizResultEditor = quizResultPref.edit();
        Map<String, ?> allEntries = quizResultPref.getAll();
        String percentString;
        if(allEntries.isEmpty()) {
            // 문제 푼 이력이 없는 경우
            percentString = "문제를 다시풀면 상승률을 알려드립니다!!";
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
                    different = (int)((gradeInt - lastCorrected) / lastCorrected * 100);
                    percentString = "지난번 성적보다 " + different + "% 상승했습니다!";
                    SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
                    int percentStartIndex = 9;
                    int percentEndIndex = percentString.length() - 1;
                    percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex - 8, 0);
                    binding.textviewQuizresultPercent.setText(percentSpannable);
                    // 새로운 점수 프리퍼런스 저장 필요

                } else if (gradeInt < lastCorrected) {
                    // 점수 하락 경우
                    different = (int)((lastCorrected - gradeInt) / lastCorrected * 100);
                    percentString = "지난번 성적보다 " + different + "% 감소했습니다ㅠ";
                    SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
                    int percentStartIndex = 9;
                    int percentEndIndex = percentString.length() - 1;
                    percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex - 8, 0);
                    binding.textviewQuizresultPercent.setText(percentSpannable);
                    // 새로운 점수 프리퍼런스 저장 필요

                } else {
                    // 점수 동일 경우
                    percentString = "지난번과 성적이 같습니다!!";
                    binding.textviewQuizresultPercent.setText(percentString);
                }
            } else {
                // 지난 성적이 0점인 경우
                percentString = "지난 성적이 0점이군요...";
                binding.textviewQuizresultPercent.setText(percentString);
            }

        }

    }

}