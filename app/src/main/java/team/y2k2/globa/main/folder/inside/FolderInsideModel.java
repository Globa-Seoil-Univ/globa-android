package team.y2k2.globa.main.folder.inside;

import java.util.ArrayList;


public class FolderInsideModel {
    private final ArrayList<FolderInsideDocsItem> items;

    public FolderInsideModel() {
        items = new ArrayList<>();
    }

    public void addItem(String folderId, String recordId, String title, String datetime) {
        items.add(new FolderInsideDocsItem(folderId, recordId, title, datetime));
    }

    public ArrayList<FolderInsideDocsItem> getItems() {
        return items;
    }
}
