package team.y2k2.globa.notification.total;

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

import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.databinding.FragmentNotificationTotalBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class TotalFragment extends Fragment {

    FragmentNotificationTotalBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList = new ArrayList<>();
    private List<TotalFragmentItem> totalFragmentItems = new ArrayList<>();
    String notificationId, profile, title, content, createdTime, notificationType;
    boolean isRead;
    TotalFragmentAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationTotalBinding.inflate(getLayoutInflater());

        initializeUI();

        return binding.getRoot();
    }

    private void initializeUI() {

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        Log.d("전체 알림", "전체 알림 조회 시작");
        notificationViewModel.getNotification("a");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {
                notificationList = notificationResponse.getNotifications();
                totalFragmentItems.clear();
                for(Notification notification : notificationList) {

                    settingNotification(notification);

                }

                adapter = new TotalFragmentAdapter(totalFragmentItems, (NotificationActivity) requireActivity(), this);

                binding.recyclerviewNotificationTotalContent.setAdapter(adapter);
                binding.recyclerviewNotificationTotalContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "전체 알림 수신 오류 : notificationResponse = null");
            }
        });
    }

    private void settingNotification(Notification notification) {
        Log.d("알림", "알림 ID : " + notification.getNotificationId());
        notificationId = notification.getNotificationId();
        notificationType = notification.getType();
        createdTime = notification.getCreatedTime().substring(0, 10);
        isRead = notification.isRead();
        switch (notificationType) {
            case "1" :
                profile = "";
                title = notification.getNotice().getTitle();
                content = notification.getNotice().getContent();
                Log.d("1번 알림", "공지 사항 알림(1번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "1", isRead));
                break;
            case "2" :
                profile = notification.getUser().getProfile();
                title = notification.getFolder().getTitle() + "폴더 공유 초대 알림";
                content = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " 폴더 공유 초대를 보냈습니다.";
                String folderId = notification.getFolder().getFolderId();
                String shareId = notification.getShare().getShareId();
                Log.d("2번 알림", "공유 초대 알림(2번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, "2", isRead));
                break;
            case "3" :
                profile = notification.getUser().getProfile();
                title = notification.getUser().getName() + "님이 공유 폴더에 문서를 추가하였습니다.";
                content = notification.getRecord().getTitle() + "가 추가됨";
                Log.d("3번 알림", "공유 폴더 파일 추가 알림(3번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "3", isRead));
                break;
            case "4" :
                profile = notification.getUser().getProfile();
                title = notification.getFolder().getTitle() + "폴더에 " + notification.getUser().getName() + "님이 초대되었습니다.";
                content = "";
                Log.d("4번 알림", "공유 폴더 사람 추가 알림(4번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "4", isRead));
                break;
            case "5" :
                profile = notification.getUser().getProfile();
                title = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + "에 댓글을 추가하였습니다.";
                content = notification.getComment().getContent();
                Log.d("5번 알림", "공유 폴더 댓글 추가 알림(5번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "5", isRead));
                break;
            case "6" :
                profile = "";
                title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                content = "";
                createdTime = notification.getCreatedTime();
                Log.d("6번 알림", "업로드 완료 알림(6번): (ID: " + notificationId + ", title: " + title + ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "6", isRead));
                break;
            case "7" :
                profile = "";
                title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                content = "";
                Log.d("7번 알림", "업로드 실패 알림(7번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "7", isRead));
                break;
            case "8" :
                profile = "";
                title = "문의 답변이 도착하였습니다.";
                content = notification.getInquiry().getTitle();
                Log.d("8번 알림", "문의 답변 알림(8번): (ID: " + notificationId + ", title: " + title + ", content: " + content +
                        ", createdTime: " + createdTime + ", isRead: " + isRead);
                totalFragmentItems.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, "", "", "8", isRead));
                break;
            default :
                break;
        }
    }

}