package team.y2k2.globa.docs.detail.highlight;

import java.util.ArrayList;


public class DocsDetailHighlightModel {

    private final ArrayList<DocsDetailHighlightItem> items;


    public DocsDetailHighlightModel() {
        items = new ArrayList<>();
    }

    public ArrayList<DocsDetailHighlightItem> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}