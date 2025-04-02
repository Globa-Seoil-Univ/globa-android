package team.y2k2.globa.main.profile.alert;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import team.y2k2.globa.R;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.AlertRequest;
import team.y2k2.globa.api.model.response.AlertResponse;

public class AlertViewModel extends ViewModel {
    private UserApiClient apiClient;
    private final MutableLiveData<AlertResponse> alertLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>();
    private final ArrayList<AlertItem> alertItems = new ArrayList<>();
    private String userId;
    private boolean uploadNotification, shareNotification, eventNotification;

    public AlertViewModel() {
        apiClient = new UserApiClient();
    }

    public LiveData<AlertResponse> getAlertLiveData() {
        return alertLiveData;
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public ArrayList<AlertItem> getAlertItems() {
        return alertItems;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        getMyAlertStatus(userId);
    }

    public void setAlertStatus(AlertResponse alertResponse) {
        uploadNotification = alertResponse.isUploadNofi();
        shareNotification = alertResponse.isShareNofi();
        eventNotification = alertResponse.isEventNofi();
        loadToggleList();
    }

    public void getMyAlertStatus(String userId) {
        AlertResponse response = apiClient.getMyAlertStatus(userId);
        alertLiveData.setValue(response);
    }

    public void requestAlertStatus(boolean uploadNofi, boolean shareNofi, boolean eventNofi) {
        AlertRequest alertRequest = new AlertRequest(uploadNofi, shareNofi, eventNofi);
        apiClient.requestAlertStatus(userId, alertRequest);
        finishActivity.setValue(true);
    }

    public void onBackButtonClick() {
        requestAlertStatus(
                alertItems.get(0).isChecked(),
                alertItems.get(1).isChecked(),
                alertItems.get(2).isChecked()
        );
    }

    private void loadToggleList() {
        alertItems.clear();
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, uploadNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, shareNotification));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, eventNotification));
    }
}