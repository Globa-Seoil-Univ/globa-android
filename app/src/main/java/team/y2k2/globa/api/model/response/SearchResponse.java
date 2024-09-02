package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Comment;
import team.y2k2.globa.api.model.entity.Record;

public class SearchResponse {

    @SerializedName("records")
    private List<Record> records;

    @SerializedName("total")
    private int total;

    public List<Record> getRecords() {
        return records;
    }

    public int getTotal() {
        return total;
    }
}
