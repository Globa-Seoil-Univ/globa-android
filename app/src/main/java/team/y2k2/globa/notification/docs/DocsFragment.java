package team.y2k2.globa.notification.docs;

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
import team.y2k2.globa.databinding.FragmentNotificationDocsBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class DocsFragment extends Fragment {

    private FragmentNotificationDocsBinding binding;
    private NotificationViewModel viewModel;
    private DocsFragmentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationDocsBinding.inflate(inflater, container, false);
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

        // API Type "r"이 문서 알림을 의미하는 것으로 추정하여 호출
        viewModel.getNotification("r");
    }

    private void setupRecyclerView() {
        adapter = new DocsFragmentAdapter(new ArrayList<>(), (NotificationActivity) requireActivity(), this);
        binding.recyclerviewNotificationDocsContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerviewNotificationDocsContent.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if (notificationResponse != null && notificationResponse.getNotifications() != null) {
                List<DocsFragmentItem> items = processNotifications(notificationResponse.getNotifications());
                adapter.setItems(items);
            } else {
                adapter.setItems(new ArrayList<>());
                Log.d("Error", "DocsFragment: notificationResponse is null");
            }
        });
    }

    private List<DocsFragmentItem> processNotifications(List<Notification> notifications) {
        List<DocsFragmentItem> items = new ArrayList<>();
        for (Notification notification : notifications) {
            // DocsFragment는 타입 6, 7만 처리
            String notificationType = notification.getType();
            if ("6".equals(notificationType) || "7".equals(notificationType)) {
                String notificationId = notification.getNotificationId();
                String createdTime = notification.getCreatedTime().substring(0, 10);
                boolean isRead = notification.isRead();
                String profile = ""; // 문서 알림은 프로필이 없을 수 있음
                String title = "";
                String content = "";

                if ("6".equals(notificationType)) {
                    title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                    content = "";
                } else { // "7"
                    title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                    content = "";
                }
                items.add(new DocsFragmentItem(notificationId, profile, title, content, createdTime, notificationType, isRead));
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