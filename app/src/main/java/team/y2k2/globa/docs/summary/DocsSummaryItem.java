package team.y2k2.globa.docs.summary;

import java.util.ArrayList;

public class DocsSummaryItem {
    private final String title;
    private final String time;
    private final ArrayList<String> descriptions;

    public DocsSummaryItem(String title, String time, ArrayList<String> descriptions) {
        this.title = title;
        this.time = time;
        this.descriptions = descriptions;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<String> getDescriptions() {
        return descriptions;
    }
}
