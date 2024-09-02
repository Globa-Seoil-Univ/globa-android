package team.y2k2.globa.api;

public class ApiModel {
    public static final String APPLICATION_JSON = "application/json";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";


    /***
     * 잘못된 요청
     */
    public static final int ERR_BAD_REQUEST = 400;
    /***
     * 만료된 인증 토큰
     */
    public static final int ERR_EXPIRED_ACCESS_TOKEN = 40010;
    /***
     * 리프레쉬 토큰이 아직 만료되지 않음
     */
    public static final int ERR_ACTIVE_REFRESH_TOKEN = 40011;
    /***
     * 리프레쉬 토큰이 일치하지 않음
     */
    public static final int ERR_NOT_MATCH_REFRESH_TOKEN = 40012;
    /***
     * 인자로 액세스 토큰이 필요함
     */
    public static final int ERR_REQUIRED_ACCESS_TOKEN = 40013;

    /***
     * 인자로 리프레쉬 토큰이 필요함
     */
    public static final int ERR_REQUIRED_REQUEST_TOKEN = 40014;

    /***
     * 인자로 유저 코드가 필요함
     */
    public static final int ERR_REQUIRED_USER_CODE = 40015;

    /***
     * 인자로 유저 아이디가 필요함
     */
    public static final int ERR_REQUIRED_USER_ID = 40016;

    /***
     * 기본 폴더는 삭제 시도 오류
     */
    public static final int ERR_FOLDER_DELETE_BAD_REQUEST = 40020;

    /***
     * 삭제된 유저를 요청할 때 발생
     */
    public static final int ERR_DELETED_USER = 40030;

    /***
     * 인자로 폴더 제목이 필요함
     */
    public static final int ERR_REQUIRED_FOLDER_TITLE = 40040;

    /***
     * 인자로 폴더 아이디가 필요함
     */
    public static final int ERR_REQUIRED_FOLDER_ID = 40041;

    /***
     * 인자로 퀴즈 아이디가 필요함
     */
    public static final int ERR_REQUIRED_QUIZ_ID = 40042;

    /***
     * 인자로 레코드 아이디가 필요함
     */
    public static final int ERR_REQUIRED_RECORD_ID = 40043;

    /***
     * postQuiz에서 인자로 퀴즈가 필요함
     */
    public static final int ERR_REQUIRED_QUIZ = 40044;

    /***
     * postRecord에서 Record 컬럼을 추가하기 위한 Dto에 부족한 값이 있을 때 발생
     */
    public static final int ERR_RECORD_POST_BAD_REQUEST = 40045;

    /***
     * 인자로 레코드 제목이 필요함
     */
    public static final int ERR_REQUIRED_RECORD_TITLE = 40046;

    /***
     * Record를 이동시킬 때, 도착지 Folder가 없음
     */
    public static final int ERR_REQUIRED_MOVE_ARRIVED_ID = 40047;

    /***
     * 초대자와 피초대자가 같은 경우 발생
     */
    public static final int ERR_INVITE_BAD_REQUEST = 40048;

    /***
     * 초대를 수락할 때, 이미 수락된 초대이면 발생
     */
    public static final int ERR_INVITE_ACCEPT_BAD_REQUEST = 40049;

    /***
     * 인자로 공지 아이디가 필요
     */
    public static final int ERR_REQUIRED_NOTICE_ID = 40050;

    /***
     * 알림을 추가할 때, 컬럼을 구성하기 위한 Dto의 부족한 값이 있을 때 발생
     */
    public static final int ERR_NOFI_POST_BAD_REQUEST = 40051;

    /***
     * 설문을 추가할 때, 컬럼을 구성하기 위한 Dto에 부족한 값이 있을 때 발생
     */
    public static final int ERR_SURVEY_POST_BAD_REQUEST = 40052;

    /***
     * 알림 검색 타입 오류
     */
    public static final int ERR_NOFI_TYPE_BAD_REQUEST = 40053;

    /***
     * 알림 아이디 필요
     */
    public static final int ERR_REQUIRED_NOTIFICATION_ID = 40054;

    /***
     * 댓글에 대댓글을 달 때, 대댓글에 대댓글을 달려고 시도할 때 발생
     */
    public static final int ERR_NOT_PARENT_COMMENT = 40060;

    /***
     * 인자로 IMAGE 파일이 필요함
     */
    public static final int ERR_REQUIRED_IMAGE = 40070;

    /***
     * 인자로 ROLE이 필요함
     */
    public static final int ERR_REQUIRED_ROLE = 40080;

    /***
     * Role의 값이 r 혹은 w 이외의 값일 때 발생
     */
    public static final int ERR_ROLE_BAD_REQUEST = 40081;

    /***
     * 인자로 SNS 종류가 필요함
     */
    public static final int ERR_REQUIRED_SNS_KIND = 40090;

    /***
     * 인자로 SNS ID가 필요함
     */
    public static final int ERR_REQUIRED_SNS_ID = 40091;

    /***
     * 인자로 이름이 필요함(별명인가)
     */
    public static final int ERR_REQUIRED_NAME = 40092;

    /***
     * SNS KIND가 1001 ~ 1004 외의 값일 때 발생
     */
    public static final int ERR_SNS_KIND_BAD_REQUEST = 40093;

    /***
     * 이름이 32자 이상일 때 발생
     */
    public static final int ERR_NAME_BAD_REQUEST = 40094;

    /***
     * 요청한 Folder Id와 DB와 일치하지 않음
     */
    public static final int ERR_MISMATCH_FOLDER_ID = 40095;

    /***
     * 권한 없음
     */
    public static final int ERR_UNAUTHORIZED = 401;

    /***
     * 유효하지 않은 토큰
     */
    public static final int ERR_INVALID_TOKEN = 40110;

    /***
     * 토큰 파싱 실패
     */
    public static final int ERR_SIGNATURE = 40120;

    /***
     * 만료된 갱신 토큰
     */
    public static final int ERR_EXPIRED_REFRESH_TOKEN = 40130;

    /***
     * 금지됨
     */
    public static final int ERR_FORBIDDEN = 403;

    /***
     * 권한 검사 시, null일 경우
     */
    public static final int ERR_NOT_NULL_ROLE = 40310;

    /***
     * 공지 작성에서 권한 검사
     */
    public static final int ERR_NOT_DESERVE_ADD_NOTICE = 40320;

    /***
     * 폴더 관련하여 접근할 때, FolderShare에 접근 권한이 없을 때 발생
     */
    public static final int ERR_NOT_DESERVE_ACCESS_FOLDER = 40321;

    /***
     * 다른 사람의 요청을 수정 시도할 때
     */
    public static final int ERR_NOT_DESERVE_MODIFY_INVITATION = 40322;

    /***
     * 섹션에 댓글을 남길 때, 유효성 검사
     */
    public static final int ERR_NOT_DESERVE_POST_COMMENT = 40323;

    /***
     * FCM을 보낼 수 없는 사용자
     */
    public static final int ERR_NOT_DESERVE_FCM = 40324;

    /***
     * 단어를 추가할 수 없는 사용자
     */
    public static final int ERR_NOT_DESERVE_DICTIONARY = 40325;

    /***
     * 토큰의 유저와 요청 유저가 다를 때
     */
    public static final int ERR_INVALID_TOKEN_USER = 40330;

    /***
     * 문의 당사자가 아닌 접근이 발생할 때
     */
    public static final int ERR_MISMATCH_INQUIRY_OWNER = 40340;

    /***
     * 폴더 소유자 아닌 접근이 발생할 때
     */
    public static final int ERR_MISMATCH_FOLDER_OWNER = 40341;

    /***
     * 댓글 작성자가 아닌 접근이 발생할 때
     */
    public static final int ERR_MISMATCH_COMMENT_OWNER = 40342;

    /***
     * 알림 관련 접근 할 때, 가져오려는 알림 PATH와 다르다면 발생
     */
    public static final int ERR_MISMATCH_NOFI_OWNER = 40343;

    /***
     * 위와 같은 상황에서 분석 GET 시도
     */
    public static final int ERR_MISMATCH_ANALYSIS_OWNER = 40344;

    /***
     * 이름 변경 시도 시, 유저가 일치하지 않음
     */
    public static final int ERR_MISMATCH_RENAME_OWNER = 40345;

    /***
     * 퀴즈의 recordId와 요청된 record의 id가 일치하지 않을 때 발생
     */
    public static final int ERR_MISMATCH_QUIZ_RECORD_ID = 40346;

    /***
     * 레코드 소유자가 아닌 접근이 발생할 때
     */
    public static final int ERR_MISMATCH_RECORD_OWNER = 40347;

    /***
     * 속한 폴더가 요청 값과 다른 경우
     */
    public static final int ERR_MISMATCH_RECORD_FOLDER = 40348;

    /***
     * 알림의 주인이 아닌 사람의 접근
     */
    public static final int ERR_MISMATCH_NOTIFICATION_OWNER = 40349;

    /***
     * 찾을 수 없음
     */
    public static final int ERR_NOT_FOUND = 404;

    /***
     * 유저를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_USER = 40410;

    /***
     * 기본 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_DEFAULT_FOLDER = 40411;

    /***
     * 타겟 유저를 찾을 수 없을 때 ( 공유 대상자라던지 )
     */
    public static final int ERR_NOT_FOUND_TARGET_USER = 40412;

    /***
     * 문의를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_INQUIRY = 40420;

    /***
     * 공지를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_NOTICE = 40430;

    /***
     * 알림을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_NOTIFICATION = 40431;

    /***
     * 문의에 대한 답변을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_ANSWER = 40440;

    /***
     * 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_FOLDER = 40450;

    /***
     * 접근 가능한 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_ACCESSIBLE_FOLDER = 40451;

    /***
     * 폴더 이동 시, 기존 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_ORIGIN_FOLDER = 40452;

    /***
     * 폴더 이동 시, 타겟 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_TARGET_FOLDER = 40453;

    /***
     * 공유를 찾을 수 없을 때, ( 어찌보면 NOT DESERVE 이지만, editInvite등을 수행할 때 이므로 별도의 에러 처리 )
     */
    public static final int ERR_NOT_FOUND_SHARE = 40454;

    /***
     * 파이어베이스에서 폴더를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_FOLDER_FIREBASE = 40455;

    /***
     * 하이라이트를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_HIGHLIGHT = 40460;

    /***
     * 부모 댓글을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_PARENT_COMMENT = 40470;

    /***
     * 레코드를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_RECORD = 40480;

    /***
     * 분석을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_ANALYSIS = 40481;

    /***
     * 퀴즈를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_QUIZ = 40482;

    /***
     * 파이어베이스에서 레코드를 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_RECORD_FIREBASE = 40483;

    /***
     * 섹션을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_SECTION = 40484;

    /***
     * 댓글을 찾을 수 없을 때
     */
    public static final int ERR_NOT_FOUND_COMMENT = 40490;

    /***
     *
     */
    public static final int ERR_DUPLICATED = 409;

    /***
     * 댓글 하이라이트 중복
     */
    public static final int ERR_HIGHLIGHT_DUPLICATED = 40910;

    /***
     * 폴더 명 중복
     */
    public static final int ERR_FOLDER_NAME_DUPLICATED = 40920;

    /***
     * 답변이 이미 있는 경우에 발생
     */
    public static final int ERR_INQUIRY_ANSWER_DUPLICATED = 40930;

    /***
     * 공유 시도 시, 이미 있거나 이미 요청을 보냈을 경우.
     */
    public static final int ERR_SHARE_USER_DUPLICATED = 40940;

    /***
     * 알림 읽음 처리 시도 시, 이미 있을 때
     */
    public static final int ERR_NOTIFICATION_READ_DUPLICATED = 40950;

    /***
     * Spring 에러
     */
    public static final int ERR_INTERNAL_SERVER_ERROR = 500;

    /***
     * 파이어베이스 파일 업로드 에러
     */
    public static final int ERR_FAILED_FILE_UPLOAD = 50010;

    /***
     * FCM 메시지 전달 에러
     */
    public static final int ERR_INTERNAL_SERVER_ERROR_50020 = 50020;

    /***
     * 레디스 타임아웃, 기존 GlobalException
     */
    public static final int ERR_REDIS_TIMEOUT = 50030;

    /***
     * 파이어베이스에 폴더 생성 오류
     */
    public static final int ERR_FAILED_FOLDER_CREATE = 50040;

    /***
     * 파이어베이스 폴더 삭제 오류
     */
    public static final int ERR_FAILED_FOLDER_DELETE = 50041;

    /***
     * 파이어베이스 통신 오류
     */
    public static final int ERR_FAILED_FIREBASE = 50050;

    /***
     * 엑셀 문서 파싱 오류
     */
    public static final int ERR_FAILED_EXCEL = 50060;

    /***
     * 키워드 엑셀을 찾을 수 없음
     */
    public static final int ERR_NOT_FOUND_KEYWORD_EXCEL = 50070;

}
