package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InquiryResponse {

    @SerializedName("inquires")
    private List<Inquiry> inquires;

    @SerializedName("total")
    private int total;

    public List<Inquiry> getInquires() {
        return inquires;
    }

    public int getTotal() {
        return total;
    }

    public static class Inquiry {
        @SerializedName("inquiryId")
        private String inquiryId;

        @SerializedName("title")
        private String title;

        @SerializedName("content")
        private String content;

        @SerializedName("createdTime")
        private String createdTime;

        @SerializedName("isSolved")
        private boolean isSolved;

        public String getInquiryId() {
            return inquiryId;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public boolean isSolved() {
            return isSolved;
        }
    }
}