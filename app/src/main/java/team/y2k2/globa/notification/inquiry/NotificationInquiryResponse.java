package team.y2k2.globa.notification.inquiry;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
