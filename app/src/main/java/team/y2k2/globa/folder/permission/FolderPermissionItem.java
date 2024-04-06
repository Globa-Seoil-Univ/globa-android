package team.y2k2.globa.folder.permission;

public class FolderPermissionItem {
    private final String name;
    private final int profileImage;

    public FolderPermissionItem(String name, int profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }


    public String getName() {
        return name;
    }

    public int getProfileImage() {
        return profileImage;
    }
}