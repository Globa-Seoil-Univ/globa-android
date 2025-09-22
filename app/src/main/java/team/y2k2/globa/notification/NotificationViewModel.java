package team.y2k2.globa.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.y2k2.globa.api.clients.FolderShareApiClient;
import team.y2k2.globa.api.clients.NotificationApiClient;
import team.y2k2.globa.api.model.response.NotificationResponse;
import team.y2k2.globa.api.model.response.UnreadNotificationCountResponse;

public class NotificationViewModel extends ViewModel {

    private FolderShareApiClient apiClient;
    private NotificationApiClient notificationApiClient;
    private final MutableLiveData<NotificationResponse> notificationLiveData = new MutableLiveData<>();
    private final MutableLiveData<UnreadNotificationCountResponse> unreadCount = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isListEmpty = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public NotificationViewModel() {
        this.apiClient = new FolderShareApiClient();
        this.notificationApiClient = new NotificationApiClient();
    }

    public LiveData<NotificationResponse> getNotificationLiveData() {
        return notificationLiveData;
    }

    public LiveData<UnreadNotificationCountResponse> getUnreadCount() {
        return unreadCount;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsListEmpty() { return isListEmpty; }

    public LiveData<Boolean> getIsLoading() { return isLoading; }


    public void getNotification(String type) {
        isLoading.setValue(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                NotificationResponse response = notificationApiClient.requestGetNotification(type, 1, 100);

                if (response != null && response.getNotifications() != null) {
                    isListEmpty.postValue(response.getNotifications().isEmpty());
                } else {
                    isListEmpty.postValue(true);
                }
                notificationLiveData.postValue(response);
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void acceptInvite(String folderId, String shareId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> apiClient.requestAcceptShareInvite(folderId, shareId));
    }

    public void denyInvite(String folderId, String shareId, String notificationId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> apiClient.requestDeniedShareInvite(folderId, shareId, notificationId));
    }

    public void getUnreadNotificationCount() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UnreadNotificationCountResponse response = notificationApiClient.getUnreadNotificationCount();
            unreadCount.postValue(response);
        });
    }

    public void readNotification(String notificationId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> notificationApiClient.updateReadNotification(notificationId));
    }
}

