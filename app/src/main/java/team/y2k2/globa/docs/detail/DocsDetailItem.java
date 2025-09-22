package team.y2k2.globa.docs.detail;

import java.util.ArrayList;
import java.util.List;
import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.docs.detail.comment.DocsDetailCommentItem;

public class DocsDetailItem {
    private final String title;
    private final String time;
    private final String sectionId;
    private final String description;
    private final List<Highlight> highlights;

    private boolean isExpanded = false;
    private List<DocsDetailCommentItem> comments = new ArrayList<>();

    public DocsDetailItem(String title, String sectionId, String time, String description, List<Highlight> highlights) {
        this.title = title;
        this.sectionId = sectionId;
        this.time = time;
        this.description = description;
        this.highlights = highlights;
    }

    // --- 기존 Getter들 ---
    public String getTitle() { return title; }
    public String getSectionId() { return sectionId; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public List<Highlight> getHighlights() { return highlights; }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public List<DocsDetailCommentItem> getComments() {
        return comments;
    }

    public void setComments(List<DocsDetailCommentItem> comments) {
        this.comments = comments;
    }
}