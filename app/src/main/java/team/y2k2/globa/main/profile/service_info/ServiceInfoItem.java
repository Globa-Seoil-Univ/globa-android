package team.y2k2.globa.main.profile.service_info;

import android.app.Activity;

public class ServiceInfoItem {
    private final int title;
    private final int description;

    private Activity activity;


    ServiceInfoItem(int title, int description) {
        this.title = title;
        this.description = description;
    }
    ServiceInfoItem(int title, int description, Activity activity) {
        this.title = title;
        this.description = description;
        this.activity = activity;
    }
    public int getDescription() {
        return description;
    }

    public int getTitle() {
        return title;
    }

    public Activity getActivity() {
        return activity;
    }
}
