package team.y2k2.globa.notification;


import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNotificationBinding;
import team.y2k2.globa.notification.notice.NoticeFragment;
import team.y2k2.globa.notification.docs.DocsFragment;
import team.y2k2.globa.notification.share.ShareFragment;
import team.y2k2.globa.notification.total.TotalFragment;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;

    private final LinearLayout[] underLines = new LinearLayout[4];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

        initializeUI();

        setContentView(binding.getRoot());
    }

    private void initializeUI() {
        underLines[0] = binding.linearlayoutNotificationTotalUnderline;
        underLines[1] = binding.linearlayoutNotificationAnnouncementUnderline;
        underLines[2] = binding.linearlayoutNotificationShareUnderline;
        underLines[3] = binding.linearlayoutNotificationDocsUnderline;

        replaceFragment(new TotalFragment());
        settingUnderline(0);

        buttonClickEvent();
    }

    private void buttonClickEvent() {

        binding.imagebuttonNotificationBack.setOnClickListener(v -> {
            finish();
        });

        binding.constraintlayoutNotificationTotal.setOnClickListener(v -> {
            replaceFragment(new TotalFragment());
            settingUnderline(0);
        });
        binding.constraintlayoutNotificationAnnouncement.setOnClickListener(v -> {
            replaceFragment(new NoticeFragment());
            settingUnderline(1);
        });
        binding.constraintlayoutNotificationShare.setOnClickListener(v -> {
            replaceFragment(new ShareFragment());
            settingUnderline(2);
        });
        binding.constraintlayoutNotificationDocs.setOnClickListener(v -> {
            replaceFragment(new DocsFragment());
            settingUnderline(3);
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_notification_content, fragment);
        fragmentTransaction.commit();
    }

    private void settingUnderline(int index) {
        for(int i = 0; i < 4; i++) {
            if(i == index) {
                underLines[i].setVisibility(View.VISIBLE);
            }
            underLines[i].setVisibility(View.INVISIBLE);
        }
    }

}
