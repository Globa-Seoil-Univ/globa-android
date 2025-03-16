package team.y2k2.globa.main.folder.add;

public class FolderAddItem {

    private final String profile;
    private final String code;
    private final String role;

    public FolderAddItem(String profile, String code, String role) {
        this.profile = profile;
        this.code = code;
        this.role = role;
    }

    public String getProfile() {
        return profile;
    }
    public String getCode() {
        return code;
    }
    public String getRole() {
        return role;
    }

}
