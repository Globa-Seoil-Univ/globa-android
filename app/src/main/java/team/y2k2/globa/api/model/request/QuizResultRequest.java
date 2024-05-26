package team.y2k2.globa.api.model.request;

import java.util.List;

import team.y2k2.globa.api.model.entity.QuizResult;

public class QuizResultRequest {

    private List<QuizResult> quizs;

    public QuizResultRequest(List<QuizResult> quizs) {
        this.quizs = quizs;
    }

    // Getters and Setters
    public List<QuizResult> getQuizs() {
        return quizs;
    }

    public void setQuizs(List<QuizResult> quizs) {
        this.quizs = quizs;
    }

}
