package team.y2k2.globa.docs.quiz.quizresult;

import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import team.y2k2.globa.databinding.ActivityQuizResultBinding;

import team.y2k2.globa.R;

public class QuizResultActivity extends AppCompatActivity {

    private ActivityQuizResultBinding binding;

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

        // 점수, 정답 수, 상승률 값 조정
        int color = Color.parseColor("#FF8F4B");
        // 점수
        String scoreString = "80점";
        SpannableStringBuilder scoreSpannable = new SpannableStringBuilder(scoreString);
        int scoreStartIndex = 0;
        int scoreEndIndex = 2;
        scoreSpannable.setSpan(new ForegroundColorSpan(color), scoreStartIndex, scoreEndIndex, 0);
        binding.textviewQuizresultScore.setText(scoreSpannable);
        // 정답 수
        String countString = "80개";
        SpannableStringBuilder countSpannable = new SpannableStringBuilder(countString);
        int countStartIndex = 0;
        int countEndIndex = 2;
        countSpannable.setSpan(new ForegroundColorSpan(color), countStartIndex, countEndIndex, 0);
        binding.textviewQuizresultCorrect.setText(countSpannable);
        // 상승률
        String percentString = "지난번 성적보다 82% 상승했습니다!";
        SpannableStringBuilder percentSpannable = new SpannableStringBuilder(percentString);
        int percentStartIndex = 9;
        int percentEndIndex = 12;
        percentSpannable.setSpan(new ForegroundColorSpan(color), percentStartIndex, percentEndIndex, 0);
        binding.textviewQuizresultPercent.setText(percentSpannable);
    }

}