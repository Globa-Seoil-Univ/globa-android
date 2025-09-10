package team.y2k2.globa.notification.share;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;
import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationShareBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class ShareFragment extends Fragment {

    private FragmentNotificationShareBinding binding;
    private NotificationViewModel viewModel;
    private ShareFragmentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationShareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupRecyclerView();
        observeViewModel();

        viewModel.getNotification("s");
    }

    private void setupRecyclerView() {
        adapter = new ShareFragmentAdapter(new ArrayList<>(), (NotificationActivity) requireActivity(), this);
        binding.recyclerviewNotificationShareContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerviewNotificationShareContent.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if (notificationResponse != null && notificationResponse.getNotifications() != null) {
                List<ShareFragmentItem> items = processNotifications(notificationResponse.getNotifications());
                adapter.setItems(items);
            } else {
                adapter.setItems(new ArrayList<>());
                Log.d("Error", "ShareFragment: notificationResponse is null");
            }
        });
    }

    private List<ShareFragmentItem> processNotifications(List<Notification> notifications) {
        List<ShareFragmentItem> items = new ArrayList<>();
        for (Notification notification : notifications) {
            String notificationType = notification.getType();
            // ShareFragment는 타입 2, 3, 4, 5 처리
            if ("2".equals(notificationType) || "3".equals(notificationType) || "4".equals(notificationType) || "5".equals(notificationType)) {
                String notificationId = notification.getNotificationId();
                String profile = notification.getUser().getProfile();
                String createdTime = notification.getCreatedTime().substring(0, 10);
                boolean isRead = notification.isRead();
                String title = "";
                String content = "";
                String folderId = "";
                String shareId = "";

                switch (notificationType) {
                    case "2":
                        title = notification.getUser().getName() + getString(R.string.fragment_share_notification_2_1) + notification.getFolder().getTitle() + getString(R.string.fragment_share_notification_2_2);
                        content = "";
                        folderId = notification.getFolder().getFolderId();
                        shareId = notification.getShare().getShareId();
                        break;
                    case "3":
                        title = notification.getUser().getName() + getString(R.string.fragment_share_notification_3_1);
                        content = notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_3_2);
                        break;
                    case "4":
                        title = notification.getFolder().getTitle() + getString(R.string.fragment_share_notification_4_1) + notification.getUser().getName() + getString(R.string.fragment_share_notification_4_2);
                        content = "";
                        break;
                    case "5":
                        title = notification.getUser().getName() + getString(R.string.fragment_share_notification_5_1) + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_5_2);
                        content = notification.getComment().getContent();
                        break;
                }
                items.add(new ShareFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, notificationType, isRead));
            }
        }
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}