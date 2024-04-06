package team.y2k2.globa.quiz;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    boolean visibiltyFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutQuizCorrect.setOnClickListener(v -> {
            // O 클릭시 작동
        });

        binding.layoutQuizWrong.setOnClickListener(v -> {
            // X 클릭시 작동
        });

        // 클리어 이펙트 재생
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(binding.imageviewQuizClear);
        Glide.with(this).load(R.drawable.clear).into(gifImage);


        // 점수, 정답 수, 상승률 값 조정
        int color = Color.parseColor("#FF8F4B");
        // 점수
        String scoreString = "80점";
        SpannableStringBuilder scoreSpannable = new SpannableStringBuilder(scoreString);
        int scoreStartIndex = 0;
        int scoreEndIndex = 2;
        scoreSpannable.setSpan(new ForegroundColorSpan(color), scoreStartIndex, scoreEndIndex, 0);
        binding.textviewQuizScore.setText(scoreSpannable);
        // 정답 수
        String countString = "80개";
        SpannableStringBuilder countSpannable = new SpannableStringBuilder(countString);
        int countStartIndex = 0;
        int countEndIndex = 2;
        countSpannable.setSpan(new ForegroundColorSpan(color), countStartIndex, countEndIndex, 0);
        binding.textviewQuizCorrect.setText(countSpannable);
        // 상승률
        String percentString = "지난번 성적보다 82% 상승했습니다!";
        SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
        int percentStartIndex = 9;
        int percentEndIndex = 12;
        percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex, 0);
        binding.textviewQuizPercent.setText(percentSpannable);


        // 임시 코드 (투명 ON/OFF)
        binding.buttonQuizBack.setOnClickListener(v -> {
            if(visibiltyFlag) {
                binding.textviewQuizCount.setVisibility(View.VISIBLE);
                binding.textviewQuizClear.setVisibility(View.INVISIBLE);
                binding.textviewQuizTimer.setVisibility(View.VISIBLE);
                binding.viewQuizTimerBackground.setVisibility(View.VISIBLE);
                binding.textviewQuizQuestion.setVisibility(View.VISIBLE);
                binding.layoutQuizCorrect.setVisibility(View.VISIBLE);
                binding.layoutQuizWrong.setVisibility(View.VISIBLE);
                binding.imageviewQuizClear.setVisibility(View.INVISIBLE);
                binding.layoutQuizTotalscore.setVisibility(View.INVISIBLE);
                binding.layoutQuizCount.setVisibility(View.INVISIBLE);
                binding.layoutQuizPercent.setVisibility(View.INVISIBLE);
                visibiltyFlag = false;
            } else {
                binding.textviewQuizCount.setVisibility(View.INVISIBLE);
                binding.textviewQuizClear.setVisibility(View.VISIBLE);
                binding.textviewQuizTimer.setVisibility(View.INVISIBLE);
                binding.viewQuizTimerBackground.setVisibility(View.INVISIBLE);
                binding.textviewQuizQuestion.setVisibility(View.INVISIBLE);
                binding.layoutQuizCorrect.setVisibility(View.INVISIBLE);
                binding.layoutQuizWrong.setVisibility(View.INVISIBLE);
                binding.imageviewQuizClear.setVisibility(View.VISIBLE);
                binding.layoutQuizTotalscore.setVisibility(View.VISIBLE);
                binding.layoutQuizCount.setVisibility(View.VISIBLE);
                binding.layoutQuizPercent.setVisibility(View.VISIBLE);
                visibiltyFlag = true;
            }
        });
    }
}