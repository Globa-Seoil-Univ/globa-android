package team.y2k2.globa.docs.detail.comment.subcomment;

public class DocsDetailSubCommentItem {

    private String profile;
    private String name;
    private String createdTime;
    private String content;
    private String commentId;
    private boolean deleted;

    public DocsDetailSubCommentItem(String profile, String name, String createdTime, String content, String commentId, boolean deleted) {
        this.profile = profile;
        this.name = name;
        this.createdTime = createdTime;
        this.content = content;
        this.commentId = commentId;
        this.deleted = deleted;
    }

    public String getProfile() {
        return profile;
    }
    public String getName() {
        return name;
    }
    public String getCreatedTime() {
        return createdTime;
    }
    public String getContent() {
        return content;
    }
    public String getCommentId() {
        return commentId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
