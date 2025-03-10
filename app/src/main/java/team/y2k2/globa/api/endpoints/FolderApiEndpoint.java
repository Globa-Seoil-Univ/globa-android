package team.y2k2.globa.api.endpoints;

public class FolderApiEndpoint {
//    폴더 삭제
    public static final String DELETE_FOLDER = "/folder/{folder_id}";
//    폴더 목록 조회
    public static final String GET_FOLDER = "/folder";
//    폴더 추가
    public static final String POST_FOLDER = "/folder";
//    폴더 이름 변경
    public static final String PATCH_FOLDER_NAME = "/folder/{folder_id}/name";
}