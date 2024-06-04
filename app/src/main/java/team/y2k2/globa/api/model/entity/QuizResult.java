package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class QuizResult {

    @SerializedName("quizId")
    private int quizId;
    @SerializedName("isCorrect")
    private boolean isCorrect;

    public QuizResult(int quizId, boolean isCorrect) {
        this.quizId = quizId;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

}
