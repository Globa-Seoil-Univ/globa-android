package team.y2k2.globa.main.folder;

import java.util.ArrayList;

public class FolderModel {

    private final ArrayList<FolderItem> items;

    public FolderModel() {
        items = new ArrayList<>();
    }

    public void addItem(String title, String datetime, int folderId) {
        items.add(new FolderItem(title, datetime, folderId));
    }

    public ArrayList<FolderItem> getItems() {
        return items;
    }
}
