package team.y2k2.globa.main.profile.alert;

import android.app.Activity;

public class AlertItem {
    private final int title;
    private final int description;
    private boolean isChecked;

    AlertItem(int title, int description, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
    }

    public int getDescription() {
        return description;
    }
    public int getTitle() {
        return title;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
