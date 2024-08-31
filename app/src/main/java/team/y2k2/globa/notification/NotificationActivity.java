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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

        binding.imagebuttonNotificationBack.setOnClickListener(v -> {
            finish();
        });

        replaceFragment(new TotalFragment());
        binding.linearlayoutNotificationTotalUnderline.setVisibility(View.VISIBLE);
        binding.linearlayoutNotificationAnnouncementUnderline.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);

        binding.constraintlayoutNotificationTotal.setOnClickListener(v -> {
            replaceFragment(new TotalFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationAnnouncementUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationAnnouncement.setOnClickListener(v -> {
            replaceFragment(new NoticeFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationAnnouncementUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationShare.setOnClickListener(v -> {
            replaceFragment(new ShareFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationAnnouncementUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.VISIBLE);
        });
        binding.constraintlayoutNotificationDocs.setOnClickListener(v -> {
            replaceFragment(new DocsFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationAnnouncementUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
        });

        setContentView(binding.getRoot());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_notification_content, fragment);
        fragmentTransaction.commit();
    }


}
