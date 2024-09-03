package team.y2k2.globa.notification.share;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationShareBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;
import team.y2k2.globa.notification.total.TotalFragmentItem;

public class ShareFragment extends Fragment {

    FragmentNotificationShareBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList = new ArrayList<>();
    private List<ShareFragmentItem> shareFragmentItems = new ArrayList<>();
    String notificationId, profile, title, content, createdTime;
    ShareFragmentAdapter adapter;
    private String notificationType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationShareBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        notificationViewModel.getNotification("s");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {
                notificationList = notificationResponse.getNotifications();
                for(Notification notification : notificationList) {

                    settingNotification(notification);

                }

                adapter = new ShareFragmentAdapter(shareFragmentItems, (NotificationActivity) requireActivity());

                binding.recyclerviewNotificationShareContent.setAdapter(adapter);
                binding.recyclerviewNotificationShareContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "알림 수신 오류");
            }
        });
    }

    private void settingNotification(Notification notification) {
        notificationId = notification.getNotificationId();
        notificationType = notification.getType();
        createdTime = notification.getCreatedTime().substring(0, 10);
        switch (notificationType) {
            case "2" :
                profile = notification.getUser().getProfile();
                title = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " 폴더 공유 초대를 보냈습니다.";
                content = "";
                String folderId = notification.getFolder().getFolderId();
                String shareId = notification.getShare().getShareId();
                Log.d("공유 알림", "공유 알림(2번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, "2"));
                break;
            case "3" :
                profile = notification.getUser().getProfile();
                title = notification.getUser().getName() + "님이 공유 폴더에 문서를 추가하였습니다.";
                content = notification.getRecord().getTitle() + "가 추가됨";
                Log.d("공유 알림", "공유 알림(3번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "3"));
                break;
            case "4" :
                profile = notification.getUser().getProfile();
                title = notification.getFolder().getTitle() + "폴더에 " + notification.getUser().getName() + "님이 초대되었습니다.";
                content = "";
                Log.d("공유 알림", "공유 알림(4번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "4"));
                break;
            case "5" :
                profile = notification.getUser().getProfile();
                title = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + "에 댓글을 추가하였습니다.";
                content = notification.getComment().getContent();
                Log.d("공유 알림", "공유 알림(5번) : (ID: " + notificationId + ", title: " + title + ", content: " + content);
                shareFragmentItems.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, "", "", "5"));
            default :
                break;
        }
    }
}