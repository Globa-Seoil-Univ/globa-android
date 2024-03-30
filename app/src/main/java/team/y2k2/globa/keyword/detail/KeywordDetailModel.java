package team.y2k2.globa.keyword.detail;

import java.util.ArrayList;

public class KeywordDetailModel {

    private final ArrayList<KeywordDetailItem> items;


    public KeywordDetailModel() {
        items = new ArrayList<>();

        items.add(new KeywordDetailItem("간선", "선을 봄.", "명사"));
        items.add(new KeywordDetailItem("간선", "가려서 뽑음.", "명사"));
        items.add(new KeywordDetailItem("간선", "'간접 선거'를 줄여 이르는 말", "명사"));
        items.add(new KeywordDetailItem("간선", "도로, 수로, 전선, 철도 따위에서 줄기가 되는 주요한 선.", "명사"));
        items.add(new KeywordDetailItem("간선", "여럿 가운데에서 골라냄.", "명사"));
        items.add(new KeywordDetailItem("간선", "그래프 이론에서 정점과 정점을 잇는 선.", "동사"));

    }

    public ArrayList<KeywordDetailItem> getItems() {
        return items;
    }
}
