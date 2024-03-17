package team.y2k2.globa.loading;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityIntroBinding;
import team.y2k2.globa.databinding.ActivityLoadingBinding;
import team.y2k2.globa.login.LoginActivity;

public class LoadingActivity extends AppCompatActivity {

//    ActivityIntroBinding binding;

    // 로딩에서 권한 불러올 수 있도록 설정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Button introStartButton = (Button) findViewById(R.id.button_intro_start);


//        SpannableStringBuilder spanTitle = new SpannableStringBuilder(binding.textviewIntroName.getText());
//        spanTitle.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        binding.textviewIntroName.setText(spanTitle);

        introStartButton.setOnClickListener(view -> loginView());
    }


    protected void loginView() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}