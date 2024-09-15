package team.y2k2.globa.notification.inquiry;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationInquiryBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;
import team.y2k2.globa.notification.notice.NoticeFragmentItem;

public class InquiryFragment extends Fragment {

    FragmentNotificationInquiryBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList = new ArrayList<>();
    private List<InquiryFragmentItem> inquiryFragmentItems = new ArrayList<>();
    String notificationId, inquiryId, title, content, createdTime;
    boolean isRead;
    InquiryFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentNotificationInquiryBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        notificationViewModel.getNotification("i");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {
                notificationList = notificationResponse.getNotifications();
                inquiryFragmentItems.clear();
                for(Notification notification : notificationList) {

                    settingNotification(notification);

                }

                adapter = new InquiryFragmentAdapter(inquiryFragmentItems, (NotificationActivity) requireActivity(), this);

                binding.recyclerviewNotificationInquiryContent.setAdapter(adapter);
                binding.recyclerviewNotificationInquiryContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "문의사항 알림 수신 오류 : notificationResponse = null");
            }
        });

    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        inquiryId = notification.getInquiry().getInquiryId();
        title = "문의에 대한 답변이 추가되었습니다.";
        content = notification.getInquiry().getTitle();
        createdTime = notification.getCreatedTime().substring(0, 10);
        isRead = notification.isRead();
        Log.d("알림", "ID: " + notificationId + ", title: " + title);
        Log.d("공지 사항 알림", "공지 사항 알림: (ID: " + notificationId + ", title: " + title + ", content: " + content +
                ", createdTime: " + createdTime + ", isRead: " + isRead);
        inquiryFragmentItems.add(new InquiryFragmentItem(notificationId, inquiryId, title, content, createdTime, isRead));
    }
}
