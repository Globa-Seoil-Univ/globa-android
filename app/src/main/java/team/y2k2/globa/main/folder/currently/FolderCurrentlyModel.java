package team.y2k2.globa.main.folder.currently;

import java.util.ArrayList;

public class FolderCurrentlyModel {

    private final ArrayList<FolderCurrentlyItem> items;
    public FolderCurrentlyModel() {
        items = new ArrayList<>();

//        items.add(new FolderCurrentlyItem("여기는 테스트","24-03-30 생성"));
    }

    public void addItem(String title, String datetime, int folderId) {
        items.add(new FolderCurrentlyItem(title, datetime, folderId));
    }

    public ArrayList<FolderCurrentlyItem> getItems() {
        return items;
    }
}
