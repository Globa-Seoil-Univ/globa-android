package team.y2k2.globa.main.profile.alert;

import android.content.Context;

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
    private final MutableLiveData<Boolean> hasChanges = new MutableLiveData<>(false);
    private boolean initialPrimary, initialUpload, initialShare, initialEvent;

    public LiveData<AlertResponse> getAlertLiveData() {
        return alertLiveData;
    }

    public LiveData<Boolean> getFinishActivity() {
        return finishActivity;
    }

    public LiveData<Boolean> getHasChanges() {
        return hasChanges;
    }

    public ArrayList<AlertItem> getAlertItems() {
        return alertItems;
    }

    public void setApiClient(UserApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setUserId(String userId) {
        getMyAlertStatus();
    }

    public void setAlertStatus(AlertResponse alertResponse) {
        initialPrimary = alertResponse.isPrimaryNofi();
        initialUpload = alertResponse.isUploadNofi();
        initialShare = alertResponse.isShareNofi();
        initialEvent = alertResponse.isEventNofi();

        loadToggleList(alertResponse);
    }

    public void getMyAlertStatus() {
        AlertResponse response = apiClient.getMyAlertStatus();
        alertLiveData.setValue(response);
    }

    public void requestAlertStatus(boolean uploadNofi, boolean shareNofi, boolean eventNofi, boolean primaryNofi) {
        AlertRequest alertRequest = new AlertRequest(uploadNofi, shareNofi, eventNofi, primaryNofi);
        apiClient.requestAlertStatus(alertRequest);
        finishActivity.setValue(true);
    }

    public void checkForChanges() {
        if (alertItems.size() < 4) return;

        boolean isChanged = (initialPrimary != alertItems.get(0).isChecked()) ||
                (initialUpload != alertItems.get(1).isChecked()) ||
                (initialShare != alertItems.get(2).isChecked()) ||
                (initialEvent != alertItems.get(3).isChecked());

        hasChanges.setValue(isChanged);
    }

    public void onSaveButtonClick() {
        if (alertItems.size() < 4) return;

        requestAlertStatus(
                alertItems.get(0).isChecked(), // primaryNofi
                alertItems.get(1).isChecked(), // uploadNofi
                alertItems.get(2).isChecked(), // shareNofi
                alertItems.get(3).isChecked()  // eventNofi
        );
    }

    public void onBackButtonClick() {
        finishActivity.setValue(true);
    }

    private void loadToggleList(AlertResponse alertResponse) {
        alertItems.clear();
        alertItems.add(new AlertItem(R.string.profile_alert_4_title, R.string.profile_alert_4_description, alertResponse.isPrimaryNofi()));
        alertItems.add(new AlertItem(R.string.profile_alert_1_title, R.string.profile_alert_1_description, alertResponse.isUploadNofi()));
        alertItems.add(new AlertItem(R.string.profile_alert_2_title, R.string.profile_alert_2_description, alertResponse.isShareNofi()));
        alertItems.add(new AlertItem(R.string.profile_alert_3_title, R.string.profile_alert_3_description, alertResponse.isEventNofi()));

        hasChanges.setValue(false);
    }
}