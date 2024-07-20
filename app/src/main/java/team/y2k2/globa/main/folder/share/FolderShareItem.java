package team.y2k2.globa.main.folder.share;

public class FolderShareItem {

    private String imageUrl;
    private int userId;
    private String role;

    public FolderShareItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImage() {
        return imageUrl;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
