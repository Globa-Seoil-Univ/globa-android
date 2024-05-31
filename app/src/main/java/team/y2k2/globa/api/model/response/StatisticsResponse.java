package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Quizgrade;
import team.y2k2.globa.api.model.entity.Studytime;


public class StatisticsResponse {

    @SerializedName("keywords")
    private List<Keyword> keywords;
    @SerializedName("studyTimes")
    private List<Studytime> studyTimes;
    @SerializedName("quizGrades")
    private List<Quizgrade> quizGrades;

    public StatisticsResponse(List<Keyword> keywords, List<Studytime> studyTimes, List<Quizgrade> quizGrades) {
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

    public List<Studytime> getStudyTimes() {
        return studyTimes;
    }
    public void setStudyTimes(List<Studytime> studyTimes) {
        this.studyTimes = studyTimes;
    }

    public List<Quizgrade> getQuizGrades() {
        return quizGrades;
    }
    public void setQuizGrades(List<Quizgrade> quizGrades) {
        this.quizGrades = quizGrades;
    }
}
