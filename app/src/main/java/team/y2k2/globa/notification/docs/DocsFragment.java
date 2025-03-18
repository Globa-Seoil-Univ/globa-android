package team.y2k2.globa.notification.docs;

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

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationDocsBinding;
import team.y2k2.globa.notification.NotificationActivity;

public class DocsFragment extends Fragment {
    FragmentNotificationDocsBinding binding;
    private final List<DocsFragmentItem> docsFragmentItems = new ArrayList<>();
    String notificationId, profile, title, content, createdTime, notificationType;
    boolean isRead;
    DocsFragmentAdapter adapter;
    ApiClient apiClient;
    String myProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationDocsBinding.inflate(getLayoutInflater());
        initializeUI();
        return binding.getRoot();
    }

    private void initializeUI() {
        apiClient = new ApiClient(binding.getRoot().getContext());
        myProfile = apiClient.requestUserInfo().getProfile();

        List<Notification> notificationList = apiClient.requestNotification("r").getNotifications();
        docsFragmentItems.clear();

        if(notificationList != null) {
            for(Notification notification : notificationList) {
                settingNotification(notification);
            }

            adapter = new DocsFragmentAdapter(docsFragmentItems, (NotificationActivity) requireActivity(), this);

            binding.recyclerviewNotificationDocsContent.setAdapter(adapter);
            binding.recyclerviewNotificationDocsContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        } else {
            Log.d(getClass().getSimpleName(), "문서 알림 오류 : notificationResponse = null");
        }

    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        notificationType = notification.getType();
        createdTime = notification.getCreatedTime().substring(0, 10);
        isRead = notification.isRead();
        switch (notificationType) {
            case "6" :
                profile = myProfile;
                title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                content = "";
                Log.d("문서 알림", "문서 알림(6번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                docsFragmentItems.add(new DocsFragmentItem(notificationId, profile, title, content, createdTime, "6", isRead));
                break;
            case "7" :
                profile = myProfile;
                title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                content = "";
                Log.d("문서 알림", "문서 알림(7번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                docsFragmentItems.add(new DocsFragmentItem(notificationId, profile, title, content, createdTime, "7", isRead));
                break;
            default :
                break;
        }
    }
}