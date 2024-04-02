package team.y2k2.globa.folder.inside;

import java.util.ArrayList;


public class FolderInsideModel {
    private final ArrayList<FolderInsideDocsItem> items;
    public FolderInsideModel() {
        items = new ArrayList<>();

        items.add(new FolderInsideDocsItem("여기는 테스트","2024-03-30 오후 12:30"));
        items.add(new FolderInsideDocsItem("나는 김인태","2024-03-30 오후 12:31"));
        items.add(new FolderInsideDocsItem("반갑습니다 GPT님","2024-03-30 오후 12:32"));
        items.add(new FolderInsideDocsItem("GPT님 축지법 쓰신다","2024-03-30 오후 12:33"));
        items.add(new FolderInsideDocsItem("장로님 에쿠스 타신다.","2024-03-30 오후 12:34"));
    }

    public ArrayList<FolderInsideDocsItem> getItems() {
        return items;
    }
}
