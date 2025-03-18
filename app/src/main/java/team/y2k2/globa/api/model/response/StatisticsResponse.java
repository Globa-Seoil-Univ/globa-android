package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.QuizGrade;
import team.y2k2.globa.api.model.entity.StudyTime;


public class StatisticsResponse {

    @SerializedName("keywords")
    private List<Keyword> keywords;
    @SerializedName("studyTimes")
    private List<StudyTime> studyTimes;
    @SerializedName("quizGrades")
    private List<QuizGrade> quizGrades;

    public StatisticsResponse(List<Keyword> keywords, List<StudyTime> studyTimes, List<QuizGrade> quizGrades) {
        this.keywords = keywords;
        this.studyTimes = studyTimes;
        this.quizGrades = quizGrades;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<StudyTime> getStudyTimes() {
        return studyTimes;
    }
    public void setStudyTimes(List<StudyTime> studyTimes) {
        this.studyTimes = studyTimes;
    }

    public List<QuizGrade> getQuizGrades() {
        return quizGrades;
    }
    public void setQuizGrades(List<QuizGrade> quizGrades) {
        this.quizGrades = quizGrades;
    }
}
