package team.y2k2.globa.notification.notice;

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

import team.y2k2.globa.api.model.entity.Notification;
import team.y2k2.globa.databinding.FragmentNotificationNoticeBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class NoticeFragment extends Fragment {

    private FragmentNotificationNoticeBinding binding;
    private NotificationViewModel viewModel;
    private NoticeFragmentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationNoticeBinding.inflate(inflater, container, false);
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

        viewModel.getNotification("n");
    }

    private void setupRecyclerView() {
        adapter = new NoticeFragmentAdapter(new ArrayList<>(), (NotificationActivity) requireActivity(), this);
        binding.recyclerviewNotificationNoticeContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerviewNotificationNoticeContent.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if (notificationResponse != null && notificationResponse.getNotifications() != null) {
                List<NoticeFragmentItem> items = processNotifications(notificationResponse.getNotifications());
                adapter.setItems(items);
            } else {
                adapter.setItems(new ArrayList<>());
                Log.d("Error", "NoticeFragment: notificationResponse is null");
            }
        });
    }

    private List<NoticeFragmentItem> processNotifications(List<Notification> notifications) {
        List<NoticeFragmentItem> items = new ArrayList<>();
        for (Notification notification : notifications) {
            // NoticeFragment는 타입 "1"만 처리
            if ("1".equals(notification.getType())) {
                String notificationId = notification.getNotificationId();
                String profile = notification.getNotice().getThumbnail();
                String createdTime = notification.getCreatedTime().substring(0, 10);
                String title = notification.getNotice().getTitle();
                String content = notification.getNotice().getContent();
                boolean isRead = notification.isRead();
                items.add(new NoticeFragmentItem(notificationId, profile, title, content, createdTime, isRead));
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