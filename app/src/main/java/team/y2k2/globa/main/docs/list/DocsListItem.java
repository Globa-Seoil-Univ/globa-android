package team.y2k2.globa.main.docs.list;

import java.util.List;

public class DocsListItem {
    private final int title;
    private final int datetime;

    private List<String> keywords;


    private int image_1;
    private int image_2;
    private int image_3;


    DocsListItem(int title, int datetime) {
        this.title = title;
        this.datetime = datetime;
    }

    public void setKeywordList(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setImage(int image_1, int image_2, int image_3) {
        this.image_1 = image_1;
        this.image_2 = image_2;
        this.image_3 = image_3;
    }

    public int getImage_1() {
        return image_1;
    }

    public int getImage_2() {
        return image_2;
    }

    public int getImage_3() {
        return image_3;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getDatetime() {
        return datetime;
    }

    public int getTitle() {
        return title;
    }

}