package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Record;


public class RecordResponse {
    @SerializedName("records")
    private List<Record> records;

    @SerializedName("total")
    private int total;

    public int getTotal() {
        return total;
    }

    public List<Record> getRecords() {
        return records;
    }
}
