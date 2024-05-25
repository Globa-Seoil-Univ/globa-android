package team.y2k2.globa.main.docs.keyword;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.Keyword;


public class DocsKeywordModel {
    private final ArrayList<DocsKeywordItem> items = new ArrayList<>();

    public DocsKeywordModel(List<Keyword> keywords) {
        for(int i = 0; i < keywords.size(); i++)
            this.items.add(new DocsKeywordItem(keywords.get(i).getWord()));
    }

    public ArrayList<DocsKeywordItem> getItems() {
        return this.items;
    }
}
