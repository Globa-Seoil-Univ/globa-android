package team.y2k2.globa.main.docs.list;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.R;

public class DocsListItemModel {
    private final ArrayList<DocsListItem> items;

    public DocsListItemModel() {
        items = new ArrayList<>();

        DocsListItem item1 = new DocsListItem(R.string.docs_title_sample_1,R.string.docs_title_datetime_1);
        List<String> keywordList_1 = new ArrayList<>();
        keywordList_1.add("글로바");
        keywordList_1.add("테스트");
        keywordList_1.add("생성형 AI");
        keywordList_1.add("서일대학교");
        item1.setKeywordList(keywordList_1);
        item1.setImage(R.drawable.profile_image_2, R.drawable.profile_image_1, R.drawable.profile_image_3);

        DocsListItem item2 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_2 = new ArrayList<>();
        keywordList_2.add("자바");
        keywordList_2.add("스레드");
        keywordList_2.add("통신");
        keywordList_2.add("예제");
        keywordList_2.add("입출력");
        keywordList_2.add("파일");
        keywordList_2.add("경로");
        item2.setKeywordList(keywordList_2);
        item2.setImage(R.drawable.profile_image_1, R.drawable.profile_image_3, R.drawable.profile_image_4);


        DocsListItem item3 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_3);
        List<String> keywordList_3 = new ArrayList<>();
        keywordList_3.add("그리드");
        keywordList_3.add("아이템");
        keywordList_3.add("테스트");
        item3.setKeywordList(keywordList_3);
        item3.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);

        DocsListItem item4 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_4 = new ArrayList<>();
        keywordList_4.add("여러");
        keywordList_4.add("개의");
        keywordList_4.add("태그");
        keywordList_4.add("테스트");

        item4.setKeywordList(keywordList_4);
        item4.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        DocsListItem item5 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_5 = new ArrayList<>();
        keywordList_5.add("문서");
        item5.setKeywordList(keywordList_5);
        item5.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        DocsListItem item6 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_6 = new ArrayList<>();
        keywordList_6.add("테스트");
        item6.setKeywordList(keywordList_6);
        item6.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        DocsListItem item7 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_7 = new ArrayList<>();
        keywordList_7.add("중");
        item7.setKeywordList(keywordList_7);
        item7.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        DocsListItem item8 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_8 = new ArrayList<>();
        keywordList_8.add("입니");
        item8.setKeywordList(keywordList_8);
        item8.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        DocsListItem item9 = new DocsListItem(R.string.docs_title_sample_2,R.string.docs_title_datetime_2);
        List<String> keywordList_9 = new ArrayList<>();
        keywordList_9.add("다");
        item9.setKeywordList(keywordList_9);
        item9.setImage(R.drawable.profile_image_3, R.drawable.profile_image_1, R.drawable.profile_image_2);


        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        items.add(item9);
    }

    public ArrayList<DocsListItem> getItems() {
        return items;
    }
}
