package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class SearchRequest {

    @SerializedName("keyword")
    private String keyword;
    private int page;
    private int count;


    public SearchRequest(String keyword, int page, int count) {
        this.keyword = keyword;
        this.page = page;
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public String getKeyword() {
        return keyword;
    }
}
