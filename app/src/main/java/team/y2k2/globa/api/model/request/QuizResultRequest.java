package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.QuizResult;

public class QuizResultRequest {

    @SerializedName("quizzes")
    private List<QuizResult> quizzes;

    public QuizResultRequest(List<QuizResult> quizzes) {
        this.quizzes = quizzes;
    }

    // Getters and Setters
    public List<QuizResult> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizResult> quizzes) {
        this.quizzes = quizzes;
    }

}
