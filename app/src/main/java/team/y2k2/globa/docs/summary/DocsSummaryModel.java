package team.y2k2.globa.docs.summary;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Section;
import team.y2k2.globa.api.model.entity.Summary;


public class DocsSummaryModel {
    private final ArrayList<DocsSummaryItem> items;

    public DocsSummaryModel(List<Section> sections) {
        items = new ArrayList<>();

        for(int i = 0; i < sections.size(); i++) {
            ArrayList<String> contents = new ArrayList<>();
            for(int j = 0; j < sections.get(i).getSummary().size(); j++) {
                Summary summary = sections.get(i).getSummary().get(j);

                contents.add(summary.getContent());
            }
            items.add(new DocsSummaryItem(sections.get(i).getTitle(), String.valueOf(sections.get(i).getStartTime()),contents));
        }
    }

    public ArrayList<DocsSummaryItem> getItems() {
        return items;
    }
}
