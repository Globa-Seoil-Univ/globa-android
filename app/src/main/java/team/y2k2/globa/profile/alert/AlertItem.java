package team.y2k2.globa.profile.alert;

import android.app.Activity;

public class AlertItem {
    private final int title;
    private final int description;

    private Activity activity;


    AlertItem(int title, int description) {
        this.title = title;
        this.description = description;
    }
    AlertItem(int title, int description, Activity activity) {
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
