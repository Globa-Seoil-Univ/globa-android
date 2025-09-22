package team.y2k2.globa.docs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.docs.detail.DocsDetailItem;
import team.y2k2.globa.api.model.entity.Section;

public class DocsModel {
    private final ArrayList<DocsDetailItem> detailItems;

    public DocsModel(List<Section> sections) {
        detailItems = new ArrayList<>();

        if (sections == null) return;

        for (Section section : sections) {
            if (section == null || section.getAnalyses() == null) continue;

            String title = section.getTitle();
            String sectionId = String.valueOf(section.getSectionId());
            int time = section.getStartTime();
            String content = section.getAnalyses().getContent();

            List<Highlight> highlights = (section.getAnalyses().getHighlights() != null)
                    ? section.getAnalyses().getHighlights()
                    : Collections.emptyList();

            detailItems.add(new DocsDetailItem(title, sectionId, String.valueOf(time), content, highlights));
        }
    }

    public ArrayList<DocsDetailItem> getDetailItems() {
        return detailItems;
    }
}