package team.y2k2.globa.myinfo;

import android.graphics.drawable.Drawable;

public class MainData {

    private String tv_title;
    private String tv_name;
    private int img_next;

    public MainData(String tv_title, String tv_name, int img_next) {
        this.tv_title = tv_title;
        this.tv_name = tv_name;
        this.img_next = img_next;
    }

    public String getTv_title() {
        return tv_title;
    }
    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }

    public String getTv_name() {
        return tv_name;
    }
    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public int getImg_next() {
        return img_next;
    }
    public void setImg_next(int img_next) {
        this.img_next = img_next;
    }
}
