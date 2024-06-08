package team.y2k2.globa.docs.detail;

import java.util.List;

import team.y2k2.globa.api.model.entity.Highlight;

public class DocsDetailItem {
    private final String title;
    private final String time;
    private final String sectionId;
    private final String description;
    private final List<Highlight> highlights;

    public DocsDetailItem(String title, String sectionId, String time, String description, List<Highlight> highlights) {
        this.title = title;
        this.sectionId = sectionId;
        this.time = time;
        this.description = description;
        this.highlights = highlights;
    }

    public String getTitle() {
        return title;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }
}
