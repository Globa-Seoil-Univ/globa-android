package team.y2k2.globa.docs.upload;

import android.content.Intent;

public class DocsUploadModel {
    private String recordName;
    private String recordExtension;
    private String recordPath;

    public DocsUploadModel(Intent intent) {
        recordName = intent.getStringExtra("recordName").split("\\.")[0];
        recordExtension = intent.getStringExtra("recordName").split("\\.")[1];
        recordPath = intent.getStringExtra("recordPath");
    }

    public String getRecordName() {
        return recordName;
    }

    public String getRecordExtension() {
        return recordExtension;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }
}
