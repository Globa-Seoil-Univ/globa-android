package team.y2k2.globa.notification.notice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.clients.NotificationApiClient;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationNoticeBinding;
import team.y2k2.globa.notification.NotificationActivity;

public class NoticeFragment extends Fragment {

    private final List<NoticeFragmentItem> noticeFragmentItems = new ArrayList<>();
    FragmentNotificationNoticeBinding binding;
    String notificationId, profile, title, content, createdTime;
    boolean isRead;
    NoticeFragmentAdapter adapter;
    NotificationApiClient apiClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationNoticeBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {
        apiClient = new NotificationApiClient();

        List<Notification> notificationList = apiClient.requestNotification("n").getNotifications();
        noticeFragmentItems.clear();

        if (notificationList != null) {
            for (Notification notification : notificationList) {

                settingNotification(notification);

            }

            adapter = new NoticeFragmentAdapter(noticeFragmentItems, (NotificationActivity) requireActivity(), this);

            binding.recyclerviewNotificationNoticeContent.setAdapter(adapter);
            binding.recyclerviewNotificationNoticeContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        } else {
            Log.d(getClass().getSimpleName(), "공지사항 알림 오류 : notificationResponse = null");
        }


    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        profile = notification.getNotice().getThumbnail();
        createdTime = notification.getCreatedTime().substring(0, 10);
        title = notification.getNotice().getTitle();
        content = notification.getNotice().getContent();
        isRead = notification.isRead();
        Log.d("공지 사항 알림", "공지 사항 알림: (ID: " + notificationId + ", title: " + title + ", content: " + content + ", createdTime: " + createdTime + ", isRead: " + isRead);
        noticeFragmentItems.add(new NoticeFragmentItem(notificationId, profile, title, content, createdTime, isRead));
    }
}