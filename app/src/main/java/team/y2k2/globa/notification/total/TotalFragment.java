package team.y2k2.globa.notification.total;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.databinding.FragmentNotificationTotalBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class TotalFragment extends Fragment {

    FragmentNotificationTotalBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList;
    private List<TotalFragmentItem> totalFragmentItems;
    String notificationId, profile, title, content, createdTime;
    TotalFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationTotalBinding.inflate(getLayoutInflater());

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        notificationViewModel.getNotification("a");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {

                notificationList = notificationResponse.getNotifications();
                for(Notification notification : notificationList) {
                    String type = notification.getType();
                    switch (type) {
                        case "1" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = notification.getNotice().getTitle();
                            content = notification.getNotice().getContent();
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "1"));
                            break;
                        case "2" :
                            notificationId = notification.getNotificationId();
                            profile = notification.getUser().getProfile();
                            title = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " 폴더 공유 초대를 보냈습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            String folderId = notification.getFolder().getFolderId();
                            String shareId = notification.getShare().getShareId();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, "2"));
                            break;
                        case "3" :
                            notificationId = notification.getNotificationId();
                            profile = notification.getUser().getProfile();
                            title = notification.getUser().getName() + "님이 공유 폴더에 문서를 추가하였습니다.";
                            content = notification.getRecord().getTitle() + "가 추가됨";
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "3"));
                            break;
                        case "4" :
                            notificationId = notification.getNotificationId();
                            profile = notification.getUser().getProfile();
                            title = notification.getFolder().getTitle() + "폴더에 " + notification.getUser().getName() + "님이 초대되었습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "4"));
                            break;
                        case "5" :
                            notificationId = notification.getNotificationId();
                            profile = notification.getUser().getProfile();
                            title = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + "에 댓글을 추가하였습니다.";
                            content = notification.getComment().getContent();
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "5"));
                            break;
                        case "6" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "6"));
                            break;
                        case "7" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "7"));
                            break;
                        case "8" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = "문의 답변이 도착하였습니다.";
                            content = notification.getInquiry().getTitle();
                            createdTime = notification.getCreatedTime();
                            totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "8"));
                            break;
                        default :
                            break;
                    }
                }

                adapter = new TotalFragmentAdapter(totalFragmentItems, (NotificationActivity) requireActivity());

                binding.recyclerviewNotificationTotalContent.setAdapter(adapter);
                binding.recyclerviewNotificationTotalContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "알림 수신 오류");
            }
        });

        return binding.getRoot();
    }

}