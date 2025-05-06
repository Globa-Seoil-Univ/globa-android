package team.y2k2.globa.notification;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    public NotificationViewModel() {
        this.apiClient = new FolderShareApiClient();
        this.notificationApiClient = new NotificationApiClient();
    }

    public MutableLiveData<NotificationResponse> getNotificationLiveData() {
        return notificationLiveData;
    }

    public MutableLiveData<UnreadNotificationCountResponse> getUnreadCount() {
        return unreadCount;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getNotification(String type) {
        NotificationResponse response = notificationApiClient.requestGetNotification(type, 1, 100);
        notificationLiveData.postValue(response);
    }

    public void acceptInvite(String folderId, String shareId) {
        apiClient.requestAcceptShareInvite(folderId, shareId);
    }

    public void denyInvite(String folderId, String shareId, String notificationId) {
        apiClient.requestDeniedShareInvite(folderId, shareId, notificationId);
    }

    public void getUnreadNotificationCount() {
        UnreadNotificationCountResponse response = notificationApiClient.getUnreadNotificationCount();
        unreadCount.postValue(response);
    }

    public void readNotification(String notificationId) {
        notificationApiClient.updateReadNotification(notificationId);
    }
}
