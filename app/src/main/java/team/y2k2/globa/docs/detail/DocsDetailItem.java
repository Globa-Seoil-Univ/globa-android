package team.y2k2.globa.docs.detail;

public class DocsDetailItem {
    private final String title;
    private final String time;
    private final String description;

    public DocsDetailItem(String title, String time, String description) {
        this.title = title;
        this.time = time;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }


}
