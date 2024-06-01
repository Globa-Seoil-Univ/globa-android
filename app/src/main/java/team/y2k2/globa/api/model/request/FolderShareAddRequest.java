package team.y2k2.globa.api.model.request;

public class FolderShareAddRequest {

    private static String role;

    public FolderShareAddRequest(String role) {
        this.role = role;
    }

    public static String getRole() {
        return role;
    }
    public static void setRole(String role) {
        FolderShareAddRequest.role = role;
    }
}
