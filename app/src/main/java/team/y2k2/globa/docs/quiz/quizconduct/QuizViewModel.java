package team.y2k2.globa.docs.quiz.quizconduct;

import static team.y2k2.globa.api.ApiClient.authorization;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team.y2k2.globa.api.ApiService;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.api.model.request.QuizResultRequest;
import team.y2k2.globa.api.model.response.QuizResponse;

public class QuizViewModel extends ViewModel {

    private ApiService apiService;
    private MutableLiveData<QuizResponse> quizLiveData;
    private MutableLiveData<String> errorLiveData;

    public QuizViewModel(ApiService apiService) {
        this.apiService = apiService;
        quizLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    public MutableLiveData<QuizResponse> getQuizLiveData() {
        return quizLiveData;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void gatherQuiz(int folderId, int recordId) {
        apiService.requestGetQuiz(folderId, recordId, "application/json", authorization).enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful()) {
                    quizLiveData.setValue(response.body());
                } else {
                    Log.e(getClass().getName(), response.message());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
            }
        });
    }

    public void submitQuizResult(int folderId, int recordId, List<QuizResult> quizResults) {
        QuizResultRequest quizRequestBody = new QuizResultRequest(quizResults);
        apiService.requestInsertQuizResult(folderId, recordId, "application/json", authorization, quizRequestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(getClass().getName(), "퀴즈 결과 전송 완료");
                } else {
                    Log.e(getClass().getName(), response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
            }
        });
    }

}
