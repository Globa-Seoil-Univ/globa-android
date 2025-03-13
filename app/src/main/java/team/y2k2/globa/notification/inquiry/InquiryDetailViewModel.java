package team.y2k2.globa.notification.inquiry;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.response.InquiryDetailResponse;

public class InquiryDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<InquiryDetailResponse> inquiryDetail = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final ApiService apiService;
    private final SharedPreferences preferences;

    public InquiryDetailViewModel(@NonNull Application application) {
        super(application);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        preferences = application.getSharedPreferences("account", android.app.Activity.MODE_PRIVATE);
    }

    public LiveData<InquiryDetailResponse> getInquiryDetail() {
        return inquiryDetail;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchInquiryDetail(String inquiryId) {
        String accessToken = "Bearer " + preferences.getString("accessToken", "");

        Call<InquiryDetailResponse> call = apiService.requestGetInquiryDetail(inquiryId, "application/json", accessToken);
        call.enqueue(new Callback<InquiryDetailResponse>() {
            @Override
            public void onResponse(Call<InquiryDetailResponse> call, Response<InquiryDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inquiryDetail.setValue(response.body());
                } else {
                    errorMessage.setValue("Error code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InquiryDetailResponse> call, Throwable t) {
                Log.d("Notification", "실패 : " + t.getMessage());
                errorMessage.setValue("네트워크 요청 실패");
            }
        });
    }
}