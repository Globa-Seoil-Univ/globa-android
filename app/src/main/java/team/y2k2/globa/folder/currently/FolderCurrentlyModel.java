package team.y2k2.globa.folder.currently;

import java.util.ArrayList;

public class FolderCurrentlyModel {

    private final ArrayList<FolderCurrentlyItem> items;
    public FolderCurrentlyModel() {
        items = new ArrayList<>();

        items.add(new FolderCurrentlyItem("여기는 테스트","24-03-30 생성"));
        items.add(new FolderCurrentlyItem("나는 김인태","24-03-30 생성"));
        items.add(new FolderCurrentlyItem("반갑습니다 GPT님","24-03-30 생성"));
        items.add(new FolderCurrentlyItem("GPT님 축지법 쓰신다","24-03-30 생성"));
    }

    public ArrayList<FolderCurrentlyItem> getItems() {
        return items;
    }
}
