package team.y2k2.globa.api.model.request;

public class FolderPermissionChangeRequest {

    private static String role;

    public static void setRole(String role) {
        FolderPermissionChangeRequest.role = role;
    }
    public static String getRole() {
        return role;
    }
}
