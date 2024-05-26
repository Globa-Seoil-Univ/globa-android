package team.y2k2.globa.api.model.entity;

public class QuizResult {

    private int quizId;
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
