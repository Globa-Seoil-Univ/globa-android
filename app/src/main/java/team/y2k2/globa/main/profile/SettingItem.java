package team.y2k2.globa.main.profile;

import android.app.Activity;

public class SettingItem {
    private final int name;
    private final int icon;

    private Activity activity;


    SettingItem(int name, int icon) {
        this.name = name;
        this.icon = icon;
    }
    SettingItem(int name, int icon, Activity activity) {
        this.name = name;
        this.icon = icon;
        this.activity = activity;
    }
    public int getIcon() {
        return icon;
    }

    public int getName() {
        return name;
    }

    public Activity getActivity() {
        return activity;
    }
}
