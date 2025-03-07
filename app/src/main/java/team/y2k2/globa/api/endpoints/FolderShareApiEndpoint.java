package team.y2k2.globa.api.endpoints;

public class FolderShareApiEndpoint {
//    공유 초대 거절
    public static final String DELETE_FOLDER_SHARE = "/folder/{folderId}/share/{shareId}";
//    사용자 공유 초대 취소
    public static final String DELETE_FOLDER_SHARE_USER = "/folder/{folderId}/share/user/{userId}";
//    공유된 사용자 조회
    public static final String GET_FOLDER_SHARE_USER = "/folder/{folderId}/share/user";
//    사용자 공유 초대 변경
    public static final String PATCH_FOLDER_SHARE_USER = "/folder/{folderId}/share/user/{userId}";
//    공유 초대 수락
    public static final String POST_FOLDER_SHARE = "/folder/{folderId}/share/{shareId}";
//    사용자 공유 초대
    public static final String POST_FOLDER_SHARE_USER = "/folder/{folderId}/share/user/{userId}";
}