package team.y2k2.globa.main.profile.info;

import android.app.Activity;

public class MyinfoItem {

    private String title;
    private String name;
    private int image;

    private Activity activity;

    public MyinfoItem(String title, String name, int image, Activity activity) {
        this.title = title;
        this.name = name;
        this.image = image;
        this.activity = activity;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Activity getActivity() {
        return activity;
    }

    public int getImage() {
        return image;
    }
}
