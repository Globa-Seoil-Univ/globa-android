package team.y2k2.globa.main.docs.list;

import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;

public class DocsListItem {
    private final String title;
    private final String datetime;
    private List<Keyword> keywords;
    private String recordId;
    private String folderId;

    private int image_1;
    private int image_2;
    private int image_3;

    public DocsListItem(String recordId, String folderId, String title, String datetime) {
        this.title = title;
        this.datetime = datetime;
        this.recordId = recordId;
        this.folderId = folderId;
    }

    public void setKeywordList(List<Keyword> keywords) {
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

    public String getRecordId() {
        return recordId;
    }

    public String getFolderId() {
        return folderId;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getTitle() {
        return title;
    }

}