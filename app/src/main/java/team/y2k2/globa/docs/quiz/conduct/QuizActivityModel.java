package team.y2k2.globa.docs.quiz.conduct;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import team.y2k2.globa.api.model.entity.Quiz;

public class QuizActivityModel extends ViewModel {

//    private final ApiService apiService;
    private final MutableLiveData<List<Quiz>> quizLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public QuizActivityModel() {
//        apiService = ApiClient.getApiService();
    }

    public MutableLiveData<List<Quiz>> getQuizLiveData() {
        return quizLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

//    public void gatherQuiz(int folderId, int recordId) {
//        apiService.requestGetQuiz(folderId, recordId, "application/json", authorization).enqueue(new Callback<List<Quiz>>() {
//            @Override
//            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
//                if (response.isSuccessful()) {
//                    List<Quiz> quizzes = response.body();
//
//                    quizLiveData.setValue(quizzes);
//                    Log.d("api 수신", "성공");
//                } else {
//                    Log.d("api 수신", "실패 : " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Quiz>> call, Throwable t) {
//                Log.d("api 송신", "실패 : " + t.getMessage());
//                Log.d("폴더id, 문서id", (folderId) + ", " + (recordId));
//            }
//        });
//    }

//    public void submitQuizResult(int folderId, int recordId, List<QuizResult> quizResults) {
//        QuizResultRequest quizRequestBody = new QuizResultRequest(quizResults);
//        apiService.requestInsertQuizResult(folderId, recordId, "application/json", authorization, quizRequestBody).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d("API 응답 성공", "퀴즈 결과 전송 완료");
//                } else {
//                    Log.d("API 응답 오류", "퀴즈 결과 전송 실패: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e("API 전송 오류", t.getMessage());
//            }
//        });
//    }
}
