package team.y2k2.globa.theme;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityThemeBinding;

public class ThemeActivity extends AppCompatActivity {

    ActivityThemeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 핸드폰 시스템 라이트/다크 모드 정보 갖고오기
        int systemMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        binding.radiogroupThemeButtongroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radiobutton_theme_lightmode) {
                    // 라이트 모드 선택

                } else if(checkedId == R.id.radiobutton_theme_darkmode) {
                    // 다크 모드 선택

                } else if(checkedId == R.id.radiobutton_theme_system) {
                    // 시스템과 동일하게 선택
                    switch (systemMode) {
                        case Configuration.UI_MODE_NIGHT_NO:
                            // 현재 시스템이 라이트 모드일 경우
                            break;
                        case Configuration.UI_MODE_NIGHT_YES:
                            // 현재 시스템이 다크 모드일 경우
                            break;
                        case Configuration.UI_MODE_NIGHT_UNDEFINED:
                            // 현재 시스템이 라이트/다크 모드를 결정할 수 없음
                            break;
                    }
                }
            }
        });

        binding.buttonThemeBack.setOnClickListener(v -> {
            finish();
        });

    }
}