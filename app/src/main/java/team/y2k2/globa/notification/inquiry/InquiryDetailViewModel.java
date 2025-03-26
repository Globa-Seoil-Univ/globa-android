package team.y2k2.globa.notification.inquiry;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import team.y2k2.globa.api.clients.InquiryApiClient;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;

public class InquiryDetailViewModel extends ViewModel {

    private final MutableLiveData<InquiryDetailResponse> inquiryDetail = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private InquiryApiClient apiClient;

    public void setApiClient(Context context) {
        this.apiClient = new InquiryApiClient();
    }

    public LiveData<InquiryDetailResponse> getInquiryDetail() {
        return inquiryDetail;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchInquiryDetail(String inquiryId) {
        InquiryDetailResponse response = apiClient.requestGetInquiryDetail(inquiryId);
        inquiryDetail.setValue(response);
    }
}