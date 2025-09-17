package team.y2k2.globa.main.profile.inquiry;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.y2k2.globa.api.clients.InquiryApiClient;
import team.y2k2.globa.api.model.response.InquiryResponse;
import team.y2k2.globa.notification.inquiry.InquiryItem;

public class InquiryViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<InquiryItem>> inquiries = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);

    private final InquiryApiClient apiClient = new InquiryApiClient();

    public LiveData<ArrayList<InquiryItem>> getInquiries() {
        return inquiries;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public void loadInquiries() {
        isLoading.setValue(true);
        isEmpty.setValue(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            InquiryResponse response = apiClient.requestGetInquiries();

            handler.post(() -> {
                isLoading.setValue(false);
                if (response != null && response.getInquires() != null && !response.getInquires().isEmpty()) {

                    ArrayList<InquiryItem> uiItems = new ArrayList<>();
                    for (InquiryResponse.Inquiry inquiry : response.getInquires()) {
                        uiItems.add(new InquiryItem(
                                inquiry.getInquiryId(),
                                inquiry.getTitle(),
                                inquiry.getContent(),
                                inquiry.getCreatedTime(),
                                inquiry.isSolved()
                        ));
                    }

                    Collections.reverse(uiItems);

                    inquiries.setValue(uiItems);
                    isEmpty.setValue(false);
                } else {
                    isEmpty.setValue(true);
                }
            });
        });
    }
}