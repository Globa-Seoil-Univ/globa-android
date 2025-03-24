package team.y2k2.globa.main.profile.alert;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.ApiClient;
import team.y2k2.globa.api.clients.UserApiClient;
import team.y2k2.globa.api.model.request.AlertRequest;
import team.y2k2.globa.api.model.response.AlertResponse;

public class AlertViewModel extends ViewModel {

    private UserApiClient apiClient;
    private final MutableLiveData<AlertResponse> alertLiveData = new MutableLiveData<>();

    public void setApiClient(Context context) {
        apiClient = new UserApiClient();
    }

    public MutableLiveData<AlertResponse> getAlertLiveData() {
        return alertLiveData;
    }

    public void getMyAlertStatus(String userId) {
        AlertResponse response = apiClient.getMyAlertStatus(userId);
        alertLiveData.setValue(response);
    }

    public void requestAlertStatus(String userId, boolean uploadNofi, boolean shareNofi, boolean eventNofi) {
        AlertRequest alertRequest = new AlertRequest(uploadNofi, shareNofi, eventNofi);
        AlertResponse response = apiClient.requestAlertStatus(userId, alertRequest);
        alertLiveData.setValue(response);
    }
}
