package team.y2k2.globa.notification;


import android.os.Bundle;

import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNotificationBinding;
import team.y2k2.globa.notification.announcement.AnnouncementFragment;
import team.y2k2.globa.notification.docs.DocsFragment;
import team.y2k2.globa.notification.share.ShareFragment;
import team.y2k2.globa.notification.total.TotalFragment;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;

    private LinearLayout[] linearLayouts = new LinearLayout[4];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

        binding.imagebuttonNotificationBack.setOnClickListener(v -> {
            finish();
        });

        loadLinearlayouts();
        replaceFragment(new TotalFragment());

        binding.constraintlayoutNotificationTotal.setOnClickListener(v -> {
            changeUnderlineColor(linearLayouts, 0);
            replaceFragment(new TotalFragment());
        });
        binding.constraintlayoutNotificationAnnouncement.setOnClickListener(v -> {
            changeUnderlineColor(linearLayouts, 1);
            replaceFragment(new AnnouncementFragment());
        });
        binding.constraintlayoutNotificationShare.setOnClickListener(v -> {
            changeUnderlineColor(linearLayouts, 2);
            replaceFragment(new ShareFragment());
        });
        binding.constraintlayoutNotificationDocs.setOnClickListener(v -> {
            changeUnderlineColor(linearLayouts, 3);
            replaceFragment(new DocsFragment());
        });

        setContentView(binding.getRoot());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_notification_content, fragment);
        fragmentTransaction.commit();
    }

    private void loadLinearlayouts() {
        linearLayouts[0] = findViewById(R.id.constraintlayout_notification_total);
        linearLayouts[1] = findViewById(R.id.constraintlayout_notification_announcement);
        linearLayouts[2] = findViewById(R.id.constraintlayout_notification_share);
        linearLayouts[3] = findViewById(R.id.constraintlayout_notification_docs);
    }

    private void changeUnderlineColor(LinearLayout[] linearLayouts, int selected) {
        for(int i = 0; i < linearLayouts.length; i++) {
            if(i == selected) {
                linearLayouts[i].setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            }
            linearLayouts[i].setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }

}
