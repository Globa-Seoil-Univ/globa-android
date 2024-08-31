package team.y2k2.globa.main.profile.info;

import android.app.Activity;

public class MyinfoItem {

    private String title;
    private String name;
    private int image;
    private String userId;

    private Activity activity;

    public MyinfoItem(String title, String name, int image, String userId, Activity activity) {
        this.title = title;
        this.name = name;
        this.image = image;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public Activity getActivity() {
        return activity;
    }

    public int getImage() {
        return image;
    }
}
