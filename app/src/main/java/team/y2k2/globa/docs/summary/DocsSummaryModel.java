package team.y2k2.globa.docs.summary;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Section;
import team.y2k2.globa.api.model.entity.Summary;
import team.y2k2.globa.docs.detail.DocsDetailItem;


public class DocsSummaryModel {
    private final ArrayList<DocsSummaryItem> items;

    public DocsSummaryModel(List<Section> sections) {
        items = new ArrayList<>();

        for(int i = 0; i < sections.size(); i++) {
            ArrayList<String> contents = new ArrayList<>();
            for(int j = 0; j < sections.get(i).getSummary().size(); j++) {
                Summary summary = sections.get(i).getSummary().get(j);

                contents.add(summary.getContent());
            }
            items.add(new DocsSummaryItem(sections.get(i).getTitle(), String.valueOf(sections.get(i).getStartTime()),contents));
        }

//        sample1.add("유엔기후변화회의에서 체결된 파리협정");
//        sample1.add("파리협정은 산업화 이전 2oC보다 낮은 수준 유지 및 기온 상승 1.5 oC로 제한");
//        sample1.add("기후변화의 위협에 대한 대응 강화 목표");
//        sample1.add("지구온난화 1.5 oC 특별보고서 공개");
//
//        items.add(new DocsSummaryItem("기후변화협약 이행", "00:01", sample1));
//
//        ArrayList<String> sample2 = new ArrayList<>();
//        sample2.add("2050년까지 ‘탄소 순배출 제로’에 도달");
//        sample2.add("2030년까지 전지구적으로 탄소배출량 절반 감축");
//        sample2.add("폭염, 가뭄 등 갑작스러운 기상현상 발생");
//        sample2.add("생물 다양성 감소, 해수면 상승");
//        sample2.add("지역적인 기후 취약성 크게 증가");
//        items.add(new DocsSummaryItem("산업혁명 이후 1.5oC 온난화 수준 안정화", "00:36", sample2));

    }

    public ArrayList<DocsSummaryItem> getItems() {
        return items;
    }
}
