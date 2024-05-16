package team.y2k2.globa.main.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import team.y2k2.globa.databinding.FragmentStatisticsBinding;

public class StatisticsFragment extends Fragment {
    String[] dayX = { "딥러닝", "학습", "지능", "데이터", "예측", "인공신경망", "사용", "입력", "패턴", "이미지" };

    FragmentStatisticsBinding binding;
    private HorizontalBarChart barChart;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());

        barChart = binding.chart;

        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart.setTouchEnabled(false); // 터치 유무
        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart.setExtraOffsets(10f, 0f, 40f, 0f);
        barChart.setFitBars(true);

        setData(); // 데이터 설정

        // XAxis (수평 막대 기준 왼쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 축 데이터 표시 위치

        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
        YAxis axisRight = barChart.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        // YAxis(Left) (수평 막대 기준 아래쪽) - 선 유무, 데이터 최솟값/최댓값, label 유무
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(true); // 기준선 활성화
        axisLeft.setDrawAxisLine(true); // 축선 활성화
        axisLeft.setAxisMinimum(0f); // 최솟값
        axisLeft.setAxisMaximum(0.5f); // 최댓값
        axisLeft.setGranularity(0.1f); // 기준선 간격 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // 기준선에 원하는 위치의 값을 추가합니다.
        axisLeft.setDrawLabels(true);
        axisLeft.setLabelCount(6, true); // 기준선의 개수를 설정합니다.
        axisLeft.setAxisLineColor(Color.parseColor("#FFFFFFFF")); // 축선 색상 설정
        axisLeft.setAxisLineWidth(2f); // 축선의 두께를 설정합니다.
        axisLeft.setGridColor(Color.parseColor("#DDDDDD")); // 기준선 색상 설정
        axisLeft.setDrawAxisLine(true); // 축선을 그립니다.
        axisLeft.setGridLineWidth(2f);
        axisLeft.setDrawGridLines(true); // 기준선을 그립니다.

        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayX));

        return binding.getRoot();
    }

    private void setData() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // 데이터 및 라벨 생성
        for (int i = 0; i < 10; i++) {
            float value = (float) Math.random() / 2;
            entries.add(new BarEntry(i, value));
            labels.add(dayX[i]); // 막대 이름 설정
        }

        // 데이터셋 생성
        BarDataSet dataSet = new BarDataSet(entries, "Data");

        // 각 막대의 색상 설정
        int[] barColors = new int[]{Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#B4622F")};
        dataSet.setColors(barColors);

        // 데이터 객체 생성
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.7f); // 막대 폭 설정

        // X축 라벨 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size()); // 모든 라벨 표시

        // 차트에 데이터 설정
        barChart.setData(data);
        barChart.invalidate(); // 차트 갱신
    }
}
