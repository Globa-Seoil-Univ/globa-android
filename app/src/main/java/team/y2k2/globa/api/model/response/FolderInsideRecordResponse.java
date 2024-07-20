package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.FolderInsideRecord;


public class FolderInsideRecordResponse {
    @SerializedName("records")
    private List<FolderInsideRecord> records;

    @SerializedName("total")
    private int total;

    public int getTotal() {
        return total;
    }

    public List<FolderInsideRecord> getRecords() {
        return records;
    }
}