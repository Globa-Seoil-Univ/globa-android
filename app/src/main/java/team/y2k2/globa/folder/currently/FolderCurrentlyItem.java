package team.y2k2.globa.folder.currently;

public class FolderCurrentlyItem {
    private final String title;
    private final String datetime;

    public FolderCurrentlyItem(String title, String datetime) {
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
