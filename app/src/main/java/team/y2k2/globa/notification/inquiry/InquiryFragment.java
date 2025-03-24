package team.y2k2.globa.notification.inquiry;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.clients.NotificationApiClient;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationInquiryBinding;
import team.y2k2.globa.notification.NotificationActivity;

public class InquiryFragment extends Fragment {

    private final List<InquiryFragmentItem> inquiryFragmentItems = new ArrayList<>();
    FragmentNotificationInquiryBinding binding;
    String notificationId, profile, inquiryId, title, content, createdTime;
    boolean isRead;
    InquiryFragmentAdapter adapter;
    NotificationApiClient apiClient;
    UserApiClient userApiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentNotificationInquiryBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {
        apiClient = new NotificationApiClient();
        userApiClient = new UserApiClient();
        profile = userApiClient.requestUserInfo().getProfile();

        List<Notification> notificationList = apiClient.requestNotification("i").getNotifications();
        inquiryFragmentItems.clear();

        if (notificationList != null) {
            for (Notification notification : notificationList) {
                settingNotification(notification);
            }

            adapter = new InquiryFragmentAdapter(inquiryFragmentItems, (NotificationActivity) requireActivity(), this);

            binding.recyclerviewNotificationInquiryContent.setAdapter(adapter);
            binding.recyclerviewNotificationInquiryContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

        } else {
            Log.d(getClass().getSimpleName(), "문의 알림 오류 : notificationResponse = null");
        }
    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        inquiryId = notification.getInquiry().getInquiryId();
        title = "문의에 대한 답변이 추가되었습니다.";
        content = notification.getInquiry().getTitle();
        createdTime = notification.getCreatedTime().substring(0, 10);
        isRead = notification.isRead();
        Log.d("문의 알림", "문의 알림: (ID: " + notificationId + ", title: " + title + ", content: " + content + ", createdTime: " + createdTime + ", isRead: " + isRead);
        inquiryFragmentItems.add(new InquiryFragmentItem(notificationId, profile, inquiryId, title, content, createdTime, isRead));
    }
}
