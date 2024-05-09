package team.y2k2.globa.main.folder.inside;

import com.google.gson.annotations.SerializedName;

import java.util.List;



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