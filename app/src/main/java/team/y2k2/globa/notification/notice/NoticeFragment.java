package team.y2k2.globa.notification.notice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationNoticeBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class NoticeFragment extends Fragment {

    FragmentNotificationNoticeBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList = new ArrayList<>();
    private List<NoticeFragmentItem> noticeFragmentItems = new ArrayList<>();
    String notificationId, title, content, createdTime;
    NoticeFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationNoticeBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        notificationViewModel.getNotification("n");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {

                notificationList = notificationResponse.getNotifications();

                for(Notification notification : notificationList) {

                    settingNotification(notification);

                }

                adapter = new NoticeFragmentAdapter(noticeFragmentItems, (NotificationActivity) requireActivity());

                binding.recyclerviewNotificationNoticeContent.setAdapter(adapter);
                binding.recyclerviewNotificationNoticeContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "알림 수신 오류");
            }
        });
    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        Log.d("알림", "ID: " + notificationId);
        title = notification.getNotice().getTitle();
        content = notification.getNotice().getContent();
        createdTime = notification.getCreatedTime().substring(0, 10);
        Log.d("공지 사항 알림", "공지 사항 알림: (ID: " + notificationId + ", title: " + title + ", content: " + content + ", createdTime: " + createdTime);
        noticeFragmentItems.add(new NoticeFragmentItem(notificationId, title, content, createdTime));
    }
}