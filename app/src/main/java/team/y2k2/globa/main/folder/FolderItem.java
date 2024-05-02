package team.y2k2.globa.main.folder;

public class FolderItem {
    private final String title;
    private final String datetime;

    public FolderItem(String title, String datetime) {
        this.title = title;
        this.datetime = datetime;
    }
    public String getTitle() {
        return title;
    }

    public String getDatetime() {
        return datetime;
    }
}
