package team.y2k2.globa.notification.docs;

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
import team.y2k2.globa.databinding.FragmentNotificationDocsBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;
import team.y2k2.globa.notification.total.TotalFragmentItem;


public class DocsFragment extends Fragment {

    FragmentNotificationDocsBinding binding;
    private NotificationViewModel notificationViewModel;
    private List<Notification> notificationList;
    private List<DocsFragmentItem> docsFragmentItems;
    String notificationId, profile, title, content, createdTime;
    DocsFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationDocsBinding.inflate(getLayoutInflater());

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        notificationViewModel.getNotification("r");

        notificationViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if(notificationResponse != null) {

                notificationList = notificationResponse.getNotifications();
                for(Notification notification : notificationList) {
                    String type = notification.getType();
                    switch (type) {
                        case "6" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            docsFragmentItems.add(new DocsFragmentItem(notificationId, profile, title, content, createdTime, "6"));
                            break;
                        case "7" :
                            notificationId = notification.getNotificationId();
                            profile = "";
                            title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                            content = "";
                            createdTime = notification.getCreatedTime();
                            docsFragmentItems.add(new DocsFragmentItem(notificationId, profile, title, content, createdTime, "7"));
                            break;
                        default :
                            break;
                    }
                }

                adapter = new DocsFragmentAdapter(docsFragmentItems, (NotificationActivity) requireActivity());

                binding.recyclerviewNotificationDocsContent.setAdapter(adapter);
                binding.recyclerviewNotificationDocsContent.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

            } else {
                Log.d("오류", "알림 수신 오류");
            }
        });

        return binding.getRoot();
    }
}