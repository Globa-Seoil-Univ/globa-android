package team.y2k2.globa.api.endpoints;

public class RecordApiEndpoint {
//    문서 링크 공유 취소
    public static final String DELETE_RECORD_SHARE_LINK = "/folder/{folderId}/record/{recordId}/link";
//    문서 삭제
    public static final String DELETE_RECORD = "/folder/{folderId}/record/{recordId}";
//    폴더 내 녹음 파일 조회
    public static final String GET_RECORD_IN_FOLDER = "/folder/{folderId}/record";
//    퀴즈 조회
    public static final String GET_RECORD_QUIZ = "/folder/{folderId}/record/{recordId}/quiz";
//    모든 녹음 파일 조회
    public static final String GET_RECORD_ALL = "/record";
//    공유 하는 문서 조회
    public static final String GET_RECORD_SHARING = "/record/sharing";
//    문서 검색
    public static final String GET_RECORD_SEARCH = "/record/search";
//    공유 받는 문서 조회
    public static final String GET_RECORD_RECEIVING = "/record/receiving";
//    녹음 파일 상세 조회
    public static final String GET_RECORD_DETAIL_IN_FOLDER = "/folder/{folderId}/record/{recordId}";
//    문서 내 시각화 자료 조회
    public static final String GET_RECORD_ANALYSIS_IN_FOLDER = "/folder/{folderId}/record/{recordId}/analysis";
//    공부 시간 수정
    public static final String PATCH_RECORD_STUDY = "/folder/{folderId}/record/{recordId}/study";
//    문서 이름 수정
    public static final String PATCH_RECORD_NAME = "/folder/{folderId}/record/{recordId}/name";
//    문서 폴더 이동
    public static final String PATCH_RECORD_MOVE_FOLDER = "/folder/{folderId}/record/{recordId}/folder";
//    문서 추가
    public static final String POST_RECORD = "/folder/{folderId}/record";
//    퀴즈 결과 추가
    public static final String POST_RECORD_QUIZ = "/folder/{folderId}/record/{recordId}/quiz";
//    문서 링크 공유
    public static final String POST_FOLDER_RECORD_LINK = "/folder/{folderId}/record/{recordId}/link";
}