package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.NotificationInquiry;

public class NotificationInquiryResponse {
    @SerializedName("inquires")
    private List<NotificationInquiry> inquires;

    @SerializedName("total")
    private int total;

    public List<NotificationInquiry> getInquires() {
        return inquires;
    }

    public int getTotal() {
        return total;
    }
}
