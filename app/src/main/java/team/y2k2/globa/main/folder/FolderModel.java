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

    public static class FolderItem {
        private final String title;
        private final String datetime;
        private final int folderId;

        public FolderItem(String title, String datetime, int folderId) {
            this.title = title;
            this.datetime = datetime;
            this.folderId = folderId;
        }

        public String getTitle() {
            return title;
        }

        public String getDatetime() {
            return datetime;
        }

        public int getFolderId() {
            return folderId;
        }
    }
} 