package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.SubComment;

public class SubCommentResponse {

    @SerializedName("comments")
    private List<SubComment> subComments;

    @SerializedName("total")
    private int total;

    public List<SubComment> getSubComments() {
        return subComments;
    }
    public int getTotal() {
        return total;
    }
}
