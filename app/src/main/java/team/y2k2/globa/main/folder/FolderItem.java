package team.y2k2.globa.main.folder;

public class FolderItem {
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
