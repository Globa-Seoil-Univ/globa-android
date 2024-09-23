package team.y2k2.globa.main.profile.service_info;

import android.app.Activity;

public class ServiceInfoItem {
    private final int title;
    private final int description;
    private final String url;


    ServiceInfoItem(int title, int description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public int getDescription() {
        return description;
    }

    public int getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
