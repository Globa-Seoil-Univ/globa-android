package team.y2k2.globa.docs.quiz.conduct;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import team.y2k2.globa.api.clients.RecordApiClient;
import team.y2k2.globa.api.model.entity.Quiz;
import team.y2k2.globa.api.model.entity.QuizResult;
import team.y2k2.globa.api.model.request.QuizResultRequest;
import team.y2k2.globa.api.model.response.QuizResponse;

public class QuizActivityModel extends ViewModel {

    private RecordApiClient apiClient;
    private final MutableLiveData<List<Quiz>> quizLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void initialize() {
        apiClient = new RecordApiClient(context);
    }


    public MutableLiveData<List<Quiz>> getQuizLiveData() {
        return quizLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void gatherQuiz(int folderId, int recordId) {
        QuizResponse quizzes = apiClient.requestGetQuiz(folderId, recordId);

        if (quizzes != null) {
            quizLiveData.setValue(quizzes.getQuizzes());
        } else {
            Log.e(getClass().getName(), "퀴즈를 불러오는 데 실패했습니다.");
            quizLiveData.setValue(null);
        }
    }

    public void submitQuizResult(int folderId, int recordId, List<QuizResult> quizResults) {
        QuizResultRequest quizRequestBody = new QuizResultRequest(quizResults);
        apiClient.requestInsertQuizResult(folderId, recordId, quizRequestBody);
    }
}

