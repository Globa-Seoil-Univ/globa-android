package team.y2k2.globa.intro;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityIntroBinding;
import team.y2k2.globa.login.LoginActivity;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        binding = ActivityIntroBinding.inflate(getLayoutInflater());

        Button introStartButton = (Button) findViewById(R.id.button_intro_start);

        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewIntroName.getText());
        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textviewIntroName.setText(spanTitle);

        introStartButton.setOnClickListener(view -> loginView());
    }


    protected void loginView() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}