package team.y2k2.globa.docs.quiz.conduct;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.api.model.request.QuizResultRequest;

public class QuizActivityModel extends ViewModel {

//    private final ApiService apiService;
    private RecordApiClient apiClient;
    private final MutableLiveData<List<Quiz>> quizLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public QuizActivityModel() {
        apiClient = new RecordApiClient();
    }

    public MutableLiveData<List<Quiz>> getQuizLiveData() {
        return quizLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void gatherQuiz(int folderId, int recordId) {
        List<Quiz> quizzes = apiClient.requestGetQuiz(folderId,recordId);
        quizLiveData.setValue(quizzes);
    }

    public void submitQuizResult(int folderId, int recordId, List<QuizResult> quizResults) {
        QuizResultRequest quizRequestBody = new QuizResultRequest(quizResults);
        apiClient.requestInsertQuizResult(folderId, recordId, quizRequestBody);
    }
}
