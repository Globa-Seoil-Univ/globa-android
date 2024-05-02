package team.y2k2.globa.main.docs.keyword;

import java.util.ArrayList;
import java.util.List;


public class DocsKeywordModel {
    private final ArrayList<DocsKeywordItem> items = new ArrayList<>();


    public DocsKeywordModel(List<String> itemsString) {
        for(int i = 0; i < itemsString.size(); i++)
            this.items.add(new DocsKeywordItem(itemsString.get(i)));
    }


    public ArrayList<DocsKeywordItem> getItems() {
        return this.items;
    }

}
