package team.y2k2.globa.folder;

import java.util.ArrayList;

public class FolderModel {

    private final ArrayList<FolderItem> items;
    public FolderModel() {
        items = new ArrayList<>();

        items.add(new FolderItem("여기는 테스트","24-03-30 생성"));
        items.add(new FolderItem("나는 김인태","24-03-30 생성"));
        items.add(new FolderItem("반갑습니다 GPT님","24-03-30 생성"));
        items.add(new FolderItem("GPT님 축지법 쓰신다","24-03-30 생성"));
    }

    public ArrayList<FolderItem> getItems() {
        return items;
    }
}
