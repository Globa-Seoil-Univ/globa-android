package team.y2k2.globa.api.model.request;

import com.google.gson.annotations.SerializedName;

public class DocsMoveRequest {
    @SerializedName("targetId")
    String targetId;

    public DocsMoveRequest(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetId() {
        return targetId;
    }
}
