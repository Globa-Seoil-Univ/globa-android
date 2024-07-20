package team.y2k2.globa.keyword.detail;

public class KeywordDetailItem {
    private final String keyword;
    private final String description;
    private final String tag;


    public KeywordDetailItem(String keyword, String description, String tag) {
        this.keyword = keyword;
        this.description = description;
        this.tag = tag;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }
}
