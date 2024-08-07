package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Quiz;

public class QuizResponse {
    @SerializedName("quizs")
    private List<Quiz> quizs;

    public List<Quiz> getQuizs() {
        return quizs;
    }
    public void setQuizs(List<Quiz> quizs) {
        this.quizs = quizs;
    }
}
