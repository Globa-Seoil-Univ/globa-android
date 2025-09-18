package team.y2k2.globa.notification.inquiry;

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
import team.y2k2.globa.databinding.FragmentNotificationInquiryBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class InquiryFragment extends Fragment {

    private FragmentNotificationInquiryBinding binding;
    private NotificationViewModel viewModel;
    private InquiryFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationInquiryBinding.inflate(inflater, container, false);
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

        viewModel.getNotification("i"); // API Type "i"가 문의 알림으로 추정
    }

    private void setupRecyclerView() {
        adapter = new InquiryFragmentAdapter(new ArrayList<>(), (NotificationActivity) requireActivity(), this);
        binding.recyclerviewNotificationInquiryContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerviewNotificationInquiryContent.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if (notificationResponse != null && notificationResponse.getNotifications() != null) {
                List<InquiryFragmentItem> items = processNotifications(notificationResponse.getNotifications());
                adapter.setItems(items);
            } else {
                adapter.setItems(new ArrayList<>());
                Log.d("Error", "InquiryFragment: notificationResponse is null");
            }
        });
    }

    private List<InquiryFragmentItem> processNotifications(List<Notification> notifications) {
        List<InquiryFragmentItem> items = new ArrayList<>();
        for (Notification notification : notifications) {
            // InquiryFragment는 타입 "8"만 처리
            if ("8".equals(notification.getType())) {
                String notificationId = notification.getNotificationId();
                String inquiryId = notification.getInquiry().getInquiryId();
                String title = "문의에 대한 답변이 추가되었습니다.";
                String content = notification.getInquiry().getTitle();
                String createdTime = notification.getCreatedTime().substring(0, 10);
                boolean isRead = notification.isRead();
                String profile = ""; // 문의 알림은 프로필이 없을 수 있음

                items.add(new InquiryFragmentItem(notificationId, profile, inquiryId, title, content, createdTime, isRead));
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