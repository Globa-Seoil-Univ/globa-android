package team.y2k2.globa.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import team.y2k2.globa.api.model.entity.Comment;

public class CommentResponse {

    @SerializedName("comments")
    private List<Comment> comments;

    @SerializedName("total")
    private int total;

    public List<Comment> getComments() {
        return comments;
    }

    public int getTotal() {
        return total;
    }
}
