package team.y2k2.globa.docs;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Highlight;
import team.y2k2.globa.api.model.entity.Section;
import team.y2k2.globa.docs.detail.DocsDetailItem;


public class DocsModel {
    private final ArrayList<DocsDetailItem> detailItems;
    private final ArrayList<Highlight> highlightItems;

    public DocsModel(List<Section> sections) {
        detailItems = new ArrayList<>();
        highlightItems = new ArrayList<Highlight>();

        for(int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            String title = section.getTitle();
            String sectionId = String.valueOf(section.getSectionId());
            int time = section.getStartTime();
            section.getSummary();
            String content = section.getAnalysis().getContent();


            for(int j = 0; j < section.getAnalysis().getHighlights().size(); j++) {
                List<Highlight> highlights = section.getAnalysis().getHighlights();

                int highlightId = highlights.get(j).getHighlightId();
                String type = highlights.get(j).getType();
                int startIdx = highlights.get(j).getStartIndex();
                int endIdx = highlights.get(j).getEndIndex();

                highlightItems.add(new Highlight(highlightId, type, startIdx, endIdx));
            }
            detailItems.add(new DocsDetailItem(title, sectionId , String.valueOf(time), content, (ArrayList<Highlight>) highlightItems.clone()));
            highlightItems.clear();
        }

//        items.add(new DocsDetailItem("기후변화협약 이행", "00:01", "유엔기후변화회의(UNFCCC,  UN  Framework  Con-vention  on  Climate  Change  2015)에서 체결된 파리협정은 지구의 평균기온 상승을 산업화 이전 대비  2 oC보다  상당히  낮은  수준(well  below  2oC)으로  유지하고,  기온 상승을  1.5 oC로 제한하도록 노력할 것을 규정하고  있으며,  기후변화협약의  이행을  증진함에  있어 기후변화의 위협에 대한 범지구적 대응 강화를 목표로 선언하고 있다. 이와 관련하여 최근 기후변화에관한 정부간 협의체(IPCC)는  ‘지구온난화  1.5 oC 특별보고서’를 공개하였다."));
//        items.add(new DocsDetailItem("산업혁명 이후 1.5oC 온난화 수준 안정화", "00:36", "보고서에 따르면 기온 상승 억제 목표인  ‘ 산업혁명 이후  1.5 oC 온난화 수준’으로 안정화  시키기  위해서는  늦어도  2050년까지  ‘ 탄소  순배출  제로’에  도달해야하며,  2030년까지  전지구적으로 탄소배출량을 절반으로 감축해야 한다. 또한 목표치인  1.5 oC에서  0.5 oC만  더  오른다고  할지라도  폭염, 가뭄 등 더욱 극심하고 갑작스러운 기상현상 및 극한기후에 노출될 가능성이 높을 뿐만 아니라, 생물다양성감소,  해수면상승,  농업  및  어업  생산량  감소  등으로 인한 지역적인 기후 취약성이 크게 증가할 것으로예상하고  있다(IPCC,  2018)."));
//        items.add(new DocsDetailItem("조금 더 길게 테스트", "01:20", "1.5/2.0oC 지구온난화에 따른 전지구, 지역규모에서 기후특성 및 극한 현상 분석에 대한 이전 연구들은 주로 결합모델비교프로젝트(The Coupled Model Intercomparison Project Phase 5, CMIP5)를 통해 산출된 미래 기후모의 자료를 기반으로 진행되어 왔다(Knutti et al., 2016; Schleussner et al., 2016; Dosio and Fischer, 2018; King and Karoly, 2017; Xu et al., 2017; Lee and Min, 2018; Tayler et al., 2018). 미래 시나리오들 가운데 타겟 온도까지 상승하며 평형을 이루는 ‘Equilibrium world’나 타겟 온도를 지나가는 ‘Transient world’에 대한 분석을 통해 지구온난화에 따른 기후시스템의 변화와 이와 관련된 영향을 이해하고자 하였다."));
//        items.add(new DocsDetailItem("조금 더 더 길게 테스트", "02:10", "예를 들면, Dosio and Fischer (2018)은 1.5oC와 2.0oC를 지나가는 RCP4.5와 RCP8.5 시나리오를 비교하여 0.5oC 상승시에 유럽대륙에서의 극한지수(Expert Team on Climate Change Detection and Indices, ETCCDI) 변화를 분석한 결과, 여름 최저온도의 경우 유럽의 70% 지역에서 상승을 나타내고 있었으나 다른 요소들의 경우 지역적인 차이가 존재함을 밝혔다. 또한, 강수량의 경우 0.5oC 상승으로 의미있는 차이를 보이지는 않았지만, 극한 강수 발생에 있어서는 큰 차이가 나타나는 것을 확인하였다. Lee and Min (2018)은 CMIP5 모델 중 1.5oC와 2oC에 수렴하는 RCP2.6, RCP4.5 시나리오들을 선택적으로 분석함으로써, 1.5oC 온난화는 2oC 온난화에 비해 열스트레스를 느끼는 동아시아 지역이 약 20% 감소 한다는 결과를 제시하였다."));
//        items.add(new DocsDetailItem("완전 길게 테스트", "03:24", "그러나 CMIP5 실험은 특정한 복사강제력에 반응하는 미래 기후 전망을 위해 설계되어 있으므로 엄밀하게 1.5oC와 2oC에 적응된 기후반응이라 보기 어렵다. 또한, 강제력에 반응하여 타겟 온도에 수렴한 CMIP5 모델 결과를 선택적으로 취합하더라도 50개 미만의 적은 앙상블 실험만으로는 극한기상에 대한 신뢰도 높은 분석을 수행하기에 한계가 있었다(Mitchell et al., 2016). 최근에는 이러한 결점을 보완하기 위해 1.5oC 및 2oC 온난화에 평형을 이루는 100개 이상의 기후 모델 앙상블 자료를 생산하여 제공하는 Half a degree Additional warming, Prognosis and Projected Impacts (HAPPI) (Mitchell et al., 2017) 프로젝트를 통한 기후 특성 및 극한기상 영향 분석이 활발히 이루어지고 있으나(Lee et al., 2018; Ruane et al., 2018; Wehner et al., 2018), 국내 적용을 위한 연구는 상대적으로 부족한 실정이다. 특히, 선행 연구들로부터 온난화에 따른 영향이 변수별, 지역별로 다르게 나타나고 있어, 신기후체제 아래 미래 기후변화에 대비한 국내 정책수립에 과학적 근거 자료로 활용하기 위해서는 동아시아 및 우리나라 지역에 대해 보다 상세한 분석이 필요하다."));
//        items.add(new DocsDetailItem("그냥 너무 길게", "06:30", "본 연구에서는 앞서 언급한 HAPPI 기후모델 자료를 활용하여 1.5/2.0oC 지구온난화에 따른 동아시아 및 우리나라 지역의 기후특성 및 극한기상 변화를 분석하고자 하였다. 각 온난화 시나리오에 따른 주요 기후변수의 평균 및 확률 특성의 변화와 0.5oC 추가 상승에 따른 기후 취약성 분석을 수행 하였으며, 이를 통해 신기후체재 대비 기후변화 적응대책 수립 지원을 위한 과학적 자료를 제공하고자 한다. 2장에서는 사용된 HAPPI 모델 자료 및 분석 변수와 방법을 제시하고, 3장에서는 HAPPI 자료에 대한 관측 모의 성능 및 기후변화 따른 주요 변수와 극한기상에 대한 영향을 분석하였다. 또한, 기온과 강수의 확률 분포의 변화 경향 및 전지구 기온의 추가 상승에 따른 지역적인 기후 취약성을 알아보았으며, 4장은 요약 및 결론 순으로 정리하였다. 본 연구에서는 기온상승 억제 목표인 전지구 1.5oC와 그보다 0.5oC가 추가 상승했을때의 동아시아 기후변화 및 기후 취약성에 대한 이해를 위해, Table 1과 같이 HAPPI (Half A degree additional warming Prognosis and Projected Impacts) 프로젝트에 참가한 다섯개의 전지구 대기모델(AGCM) 실험 결과를 이용하였다. 각 모델은 현재 기후(2006~2015년, All-HIST)와 산업혁명 이후 1.5oC 온난화 (Plus15-Future) 및 2oC 온난화(Plus20-Future) 시나리오에 대해 10년 동안의 타임슬라이스 실험을 수행하였다(Mitchell et al., 2017). 현재기후 실험에서 2006~2015년 해수면온도 및 해빙은 관측자료로부터 처방하고 있으며, 1.5oC와 2oC 온난화 시나리오에서의 해수면온도는 RCP2.6 및 RCP4.5 시나리오 기반으로 CMIP5 23개 다중모델로부터 현재 대비 증가한 패턴을 계산하여 관측자료에 더하여 처방하였다. 다만 해빙의 경우는 현재 기후에 대한 CMIP5 모델의 낮은 재현성으로 인해 미래 전망에 대한 신뢰가 낮기 때문에, 해수면온도와 해빙 간의 선형 관계를 이용하여 온난화 시나리오에서의 극지역 해빙을 사용하였다. 각 실험은 모델 내부 변동성에 대한 불확실성을 설명하기 위해 많은 앙상블 멤버를 포함하고 있으며, 본 연구에서는 100개 이상의 앙상블멤버를 가진 모델들을 분석하였다."));
    }

    public ArrayList<DocsDetailItem> getDetailItems() {
        return detailItems;
    }

    public ArrayList<Highlight> getHighlightItems() {
        return highlightItems;
    }
}
