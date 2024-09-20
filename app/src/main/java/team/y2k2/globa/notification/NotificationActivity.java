package team.y2k2.globa.notification;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import team.y2k2.globa.R;
import team.y2k2.globa.databinding.ActivityNotificationBinding;
import team.y2k2.globa.notification.inquiry.InquiryFragment;
import team.y2k2.globa.notification.notice.NoticeFragment;
import team.y2k2.globa.notification.docs.DocsFragment;
import team.y2k2.globa.notification.share.ShareFragment;
import team.y2k2.globa.notification.total.TotalFragment;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;
    NotificationViewModel notificationViewModel;
    String unreadAll, unreadNotice, unreadShare, unreadDocument, unreadInquiry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());

//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//           if(task.isSuccessful()) {
//               Log.d("FCM 토큰", "FCM 토큰 수령 성공 : " + task.getResult());
//           } else {
//               Log.d("FCM 토큰", "FCM 토큰 수령 실패 : " + task.getException());
//           }
//        });

        initializeUI();

        setContentView(binding.getRoot());
    }

    private void initializeUI() {

        replaceFragment(new TotalFragment());

        binding.linearlayoutNotificationTotalUnderline.setVisibility(View.VISIBLE);
        binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
        binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.INVISIBLE);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.getUnreadNotificationCount();
        notificationViewModel.getUnreadCount().observe(this, unreadCount -> {
            if(unreadCount != null) {
                unreadAll = unreadCount.getAll();
                unreadNotice = unreadCount.getNotice();
                unreadShare = unreadCount.getShare();
                unreadDocument = unreadCount.getDocument();
                unreadInquiry = unreadCount.getInquiry();
                Log.d("안 읽은 알림 개수", "total: " + unreadAll + ", notice: " + unreadNotice + ", share: " + unreadShare +
                        ", docs: " + unreadDocument + ", inquiry: " + unreadInquiry);
                binding.textviewNotificationTotalCount.setText(unreadAll);
                binding.textviewNotificationNoticeCount.setText(unreadNotice);
                binding.textviewNotificationShareCount.setText(unreadShare);
                binding.textviewNotificationDocsCount.setText(unreadDocument);
                binding.textviewNotificationInquiryCount.setText(unreadInquiry);
            } else {
                Log.d(getClass().getName(), "안 읽은 알림 개수 가져오기 오류 unreadCount = null");
            }
        });

        buttonClickEvent();
    }

    private void buttonClickEvent() {

        binding.imagebuttonNotificationBack.setOnClickListener(v -> {
            finish();
        });

        binding.constraintlayoutNotificationTotal.setOnClickListener(v -> {
            replaceFragment(new TotalFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationNotice.setOnClickListener(v -> {
            replaceFragment(new NoticeFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationShare.setOnClickListener(v -> {
            replaceFragment(new ShareFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationDocs.setOnClickListener(v -> {
            replaceFragment(new DocsFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.VISIBLE);
            binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.INVISIBLE);
        });
        binding.constraintlayoutNotificationInquiry.setOnClickListener(v -> {

            replaceFragment(new InquiryFragment());
            binding.linearlayoutNotificationTotalUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationNoticeUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationShareUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationDocsUnderline.setVisibility(View.INVISIBLE);
            binding.linearlayoutNotificationInquiryUnderline.setVisibility(View.VISIBLE);
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_notification_content, fragment);
        fragmentTransaction.commit();
    }

}
