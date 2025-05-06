package team.y2k2.globa.notification.share;

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

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.NotificationApiClient;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationShareBinding;
import team.y2k2.globa.notification.NotificationActivity;

public class ShareFragment extends Fragment {
    private final List<ShareFragmentItem> shareFragmentItems = new ArrayList<>();
    FragmentNotificationShareBinding binding;
    String notificationId, profile, title, content, createdTime, notificationType;
    boolean isRead;
    ShareFragmentAdapter adapter;
    NotificationApiClient apiClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationShareBinding.inflate(getLayoutInflater());
        initializeUI();
        return binding.getRoot();
    }

    private void initializeUI() {
        apiClient = new NotificationApiClient();

        List<Notification> notificationList = apiClient.requestNotification("s").getNotifications();
        shareFragmentItems.clear();

        if (notificationList != null) {
            for (Notification notification : notificationList) {
                settingNotification(notification);
            }

            adapter = new ShareFragmentAdapter(shareFragmentItems, (NotificationActivity) requireActivity(), this);

            binding.recyclerviewNotificationShareContent.setAdapter(adapter);
            binding.recyclerviewNotificationShareContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        } else {
            Log.d(getClass().getSimpleName(), "공유 알림 오류 : notificationResponse = null");
        }
    }

    private void settingNotification(Notification notification) {
        profile = notification.getUser().getProfile();
        notificationId = notification.getNotificationId();
        notificationType = notification.getType();
        createdTime = notification.getCreatedTime().substring(0, 10);
        isRead = notification.isRead();
        switch (notificationType) {
            case "2":
                title = notification.getUser().getName() + getString(R.string.fragment_share_notification_2_1) + notification.getFolder().getTitle() + getString(R.string.fragment_share_notification_2_2);
                content = "";
                String folderId = notification.getFolder().getFolderId();
                String shareId = notification.getShare().getShareId();
                Log.d("공유 알림", "공유 알림(2번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, "2", isRead));
                break;
            case "3":
                title = notification.getUser().getName() + getString(R.string.fragment_share_notification_3_1);
                content = notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_3_2);
                Log.d("공유 알림", "공유 알림(3번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "3", isRead));
                break;
            case "4":
                title = notification.getFolder().getTitle() + getString(R.string.fragment_share_notification_4_1) + notification.getUser().getName() + getString(R.string.fragment_share_notification_4_2);
                content = "";
                Log.d("공유 알림", "공유 알림(4번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "4", isRead));
                break;
            case "5":
                title = notification.getUser().getName() + getString(R.string.fragment_share_notification_5_1) + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_5_2);
                content = notification.getComment().getContent();
                Log.d("공유 알림", "공유 알림(5번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "5", isRead));
            default:
                break;
        }
    }
}