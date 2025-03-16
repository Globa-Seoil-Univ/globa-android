package team.y2k2.globa.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import team.y2k2.globa.api.model.entity.Record;
import team.y2k2.globa.api.model.response.*;

public class ApiUnitTest {
    private ApiClient apiClient;

    @Before
    public void setUp() {
        /* 앱 빌드 후 Access Token 발급받아 값 입력 후 테스트를 진행합니다. */
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5IiwiZXhwIjoxNzQxNDUwNjY5fQ.6KG6zSTsu04Yf4Vt3XvUjz1n_OsUTpBxZ5C_7JniOLM";
        this.apiClient = new ApiClient(accessToken);
    }

    @Test
    public void getRecords() {
        RecordResponse response = apiClient.requestGetRecords(10);
        List<Record> records = response.getRecords();

        for (Record record : records) {
            assertNotNull(record.getTitle());
            assertNotNull(record.getCreatedTime());
            assertNotNull(record.getFolderId());
            assertNotNull(record.getKeywords());
            assertNotNull(record.getRecordId());
        }
    }

    @Test
    public void getUser() {
        UserInfoResponse response = apiClient.requestUserInfo();

        assertNotNull(response.getUserId());
        assertNotNull(response.getName());
        assertNotNull(response.getCode());
        assertNotNull(response.getProfile());
        assertNotNull(response.getPublicFolderId());

        assertThat("Code는 6자리 숫자 형태 입니다.", response.getCode().length() == 6);
        assertThat("publicFolderId는 숫자 형태 입니다.", Integer.parseInt(response.getPublicFolderId()) > 0);
    }
}
