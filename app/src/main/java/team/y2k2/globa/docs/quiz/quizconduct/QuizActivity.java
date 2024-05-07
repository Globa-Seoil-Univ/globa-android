package team.y2k2.globa.docs.quiz.quizconduct;

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

    }
}