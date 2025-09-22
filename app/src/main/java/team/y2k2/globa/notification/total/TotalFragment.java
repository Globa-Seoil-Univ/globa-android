package team.y2k2.globa.notification.total;

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
import team.y2k2.globa.databinding.FragmentNotificationTotalBinding;
import team.y2k2.globa.notification.NotificationActivity;
import team.y2k2.globa.notification.NotificationViewModel;

public class TotalFragment extends Fragment {

    private FragmentNotificationTotalBinding binding;
    private NotificationViewModel viewModel;
    private TotalFragmentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationTotalBinding.inflate(inflater, container, false);
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

        viewModel.getNotification("a");
    }

    private void setupRecyclerView() {
        adapter = new TotalFragmentAdapter(new ArrayList<>(), (NotificationActivity) requireActivity(), this);
        binding.recyclerviewNotificationTotalContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerviewNotificationTotalContent.setAdapter(adapter);
    }

    private void observeViewModel() {
        // 로딩 상태 관찰
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressbarTotalLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                // 로딩 중에는 리스트를 숨겨서 사용자 상호작용을 방지
                binding.recyclerviewNotificationTotalContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), notificationResponse -> {
            if (notificationResponse != null && notificationResponse.getNotifications() != null) {
                List<TotalFragmentItem> items = processNotifications(notificationResponse.getNotifications());
                adapter.setItems(items);
            } else {
                adapter.setItems(new ArrayList<>());
                Log.d("Error", "TotalFragment: notificationResponse is null");
            }
        });

        viewModel.getIsListEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty != null && binding.textviewTotalEmpty != null) {
                binding.textviewTotalEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        });
    }

    private List<TotalFragmentItem> processNotifications(List<Notification> notifications) {
        List<TotalFragmentItem> items = new ArrayList<>();
        for (Notification notification : notifications) {
            String notificationId = notification.getNotificationId();
            String notificationType = notification.getType();
            String createdTime = notification.getCreatedTime().substring(0, 10);
            boolean isRead = notification.isRead();
            String profile = "";
            String title = "";
            String content = "";
            String folderId = "";
            String shareId = "";
            String inquiryId = "";
            String recordId = "";

            switch (notificationType) {
                case "1":
                    profile = notification.getNotice().getThumbnail();
                    title = notification.getNotice().getTitle();
                    content = notification.getNotice().getContent();
                    break;
                case "2":
                    profile = notification.getUser().getProfile();
                    title = notification.getFolder().getTitle() + "폴더 공유 초대 알림";
                    content = notification.getUser().getName() + "님이 " + notification.getFolder().getTitle() + " 폴더 공유 초대를 보냈습니다.";
                    folderId = notification.getFolder().getFolderId();
                    shareId = notification.getShare().getShareId();
                    break;
                case "3":
                    profile = notification.getUser().getProfile();
                    title = notification.getUser().getName() + getString(R.string.fragment_share_notification_3_1);
                    content = notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_3_2);
                    break;
                case "4":
                    content = "";
                    break;
                case "5":
                    profile = notification.getUser().getProfile();
                    title = notification.getUser().getName() + getString(R.string.fragment_share_notification_5_1) + notification.getFolder().getTitle() + " - " + notification.getRecord().getTitle() + getString(R.string.fragment_share_notification_5_2);
                    content = notification.getComment().getContent();
                    break;
                case "6":
                    title = notification.getFolder().getTitle() + "폴더에 " + notification.getRecord().getTitle() + "문서가 추가되었습니다.";
                    content = "";
                    folderId = (notification.getFolder() != null) ? notification.getFolder().getFolderId() : "";
                    recordId = (notification.getRecord() != null) ? notification.getRecord().getRecordId() : "";
                    break;
                case "7":
                    title = notification.getFolder().getTitle() + "폴더에 문서 추가를 실패하였습니다.";
                    content = "";
                    folderId = (notification.getFolder() != null) ? notification.getFolder().getFolderId() : "";
                    break;
                case "8":
                    title = "문의 답변이 도착하였습니다.";
                    content = notification.getInquiry().getTitle();
                    inquiryId = notification.getInquiry().getInquiryId();
                    break;
            }
            items.add(new TotalFragmentItem(notificationId, profile, title, content, createdTime, folderId, shareId, inquiryId, recordId, notificationType, isRead));
        }
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

