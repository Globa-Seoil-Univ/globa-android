package team.y2k2.globa.main.folder.permission;

public class FolderPermissionItem {
    private final String name;
    private final String profileImageUrl;
    private int selectedOption;

    public FolderPermissionItem(String name, String profileImageUrl, int selectedOption) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.selectedOption = selectedOption;
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
}