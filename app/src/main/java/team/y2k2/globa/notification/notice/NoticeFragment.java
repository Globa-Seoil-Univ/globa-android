package team.y2k2.globa.notification.notice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationNoticeBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class NoticeFragment extends Fragment {

    FragmentNotificationNoticeBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList;
    private List<NoticeFragmentItem> noticeFragmentItems;
    String notificationId, title, content, createdTime;
    NoticeFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationNoticeBinding.inflate(getLayoutInflater());

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        notificationViewModel.getNotification("n");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {

                notificationList = notificationResponse.getNotifications();

                for(Notification notification : notificationList) {
                    notificationId = notification.getNotificationId();
                    title = notification.getNotice().getTitle();
                    content = notification.getNotice().getContent();
                    createdTime = notification.getCreatedTime();
                    noticeFragmentItems.add(new NoticeFragmentItem(notificationId, title, content, createdTime));
                }

                adapter = new NoticeFragmentAdapter(noticeFragmentItems, (NotificationActivity) requireActivity());

                binding.recyclerviewNotificationNoticeContent.setAdapter(adapter);
                binding.recyclerviewNotificationNoticeContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "알림 수신 오류");
            }
        });

        return binding.getRoot();
    }
}