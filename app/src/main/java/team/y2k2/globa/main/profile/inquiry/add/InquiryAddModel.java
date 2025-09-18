package team.y2k2.globa.main.profile.inquiry.add;

public class InquiryAddModel {
    private String title;
    private String description;

    public InquiryAddModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}