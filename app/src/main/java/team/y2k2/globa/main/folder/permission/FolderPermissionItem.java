package team.y2k2.globa.main.folder.permission;

public class FolderPermissionItem {
    private final String name;
    private final String profileImageUrl;
    private int selectedOption;
    private final int shareId;
    private final int userId;

    public FolderPermissionItem(String name, String profileImageUrl, int selectedOption, int shareId, int userId) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.selectedOption = selectedOption;
        this.shareId = shareId;
        this.userId = userId;
    }


    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getShareId() {
        return shareId;
    }

    public int getUserId() {
        return userId;
    }
}