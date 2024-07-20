package team.y2k2.globa.api.model.entity;

import com.google.gson.annotations.SerializedName;

public class Quiz {

    @SerializedName("quizId")
    private int quizId;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private int answer;

    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }
    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
