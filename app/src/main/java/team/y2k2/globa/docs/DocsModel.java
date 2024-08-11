package team.y2k2.globa.docs;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.api.model.entity.Section;
import team.y2k2.globa.docs.detail.DocsDetailItem;

public class DocsModel {
    private final ArrayList<DocsDetailItem> detailItems;
    private final ArrayList<Highlight> highlightItems;

    public DocsModel(List<Section> sections) {
        detailItems = new ArrayList<>();
        highlightItems = new ArrayList<Highlight>();

        for(int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            String title = section.getTitle();
            String sectionId = String.valueOf(section.getSectionId());
            int time = section.getStartTime();
            section.getSummary();
            String content = section.getAnalysis().getContent();

            for(int j = 0; j < section.getAnalysis().getHighlights().size(); j++) {
                List<Highlight> highlights = section.getAnalysis().getHighlights();

                int highlightId = highlights.get(j).getHighlightId();
                String type = highlights.get(j).getType();
                int startIdx = highlights.get(j).getStartIndex();
                int endIdx = highlights.get(j).getEndIndex();

                highlightItems.add(new Highlight(highlightId, type, startIdx, endIdx));
            }
            detailItems.add(new DocsDetailItem(title, sectionId , String.valueOf(time), content, (ArrayList<Highlight>) highlightItems.clone()));
            highlightItems.clear();
        }
    }

    public ArrayList<DocsDetailItem> getDetailItems() {
        return detailItems;
    }
}
