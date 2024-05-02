package team.y2k2.globa.docs.detail.highlight;

import java.util.ArrayList;

import team.y2k2.globa.R;

public class DocsDetailHighlightModel {

    private final ArrayList<DocsDetailHighlightItem> items;


    public DocsDetailHighlightModel() {
        items = new ArrayList<>();

        items.add(new DocsDetailHighlightItem(212, 214, R.color.secondary));
        items.add(new DocsDetailHighlightItem(295, 314, R.color.red));

    }

    public ArrayList<DocsDetailHighlightItem> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}