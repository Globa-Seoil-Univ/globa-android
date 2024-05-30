package team.y2k2.globa.docs.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Quizgrade;
import team.y2k2.globa.api.model.entity.Studytime;
import team.y2k2.globa.databinding.ActivityDocsStatisticsBinding;

public class DocsStatisticsActivity extends AppCompatActivity {

    String[] wordX, timeX, gradeX;
    int[] wordValues, timeValues, gradeValues;
    ActivityDocsStatisticsBinding binding;
    private HorizontalBarChart docsBarChart;
    private LineChart docsTimeLineChart, docsGradeLineChart;
    DocsStatisticsViewModel docsStatisticsViewModel;
    int folderId, recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        folderId = Integer.parseInt(getIntent().getStringExtra("folderId"));
        recordId = Integer.parseInt(getIntent().getStringExtra("recordId"));

        docsStatisticsViewModel = new ViewModelProvider(this).get(DocsStatisticsViewModel.class);
        docsStatisticsViewModel.getDocsStatistics(folderId, recordId);

        docsStatisticsViewModel.getDocsStatisticsLiveData().observe(this, docsStatistics -> {
            if(docsStatistics != null) {
                List<Keyword> keywords = docsStatistics.getKeywords();
                List<Studytime> studytimes = docsStatistics.getStudyTimes();
                List<Quizgrade> quizgrades = docsStatistics.getQuizGrades();

                wordX = keywords.stream().map(Keyword::getWord).toArray(String[]::new);
                wordValues = keywords.stream().mapToInt(Keyword::getImportance).toArray();

                timeX = studytimes.stream().map(Studytime::getCreatedTime).toArray(String[]::new);
                timeValues = studytimes.stream().mapToInt(Studytime::getStudyTime).toArray();

                gradeX = quizgrades.stream().map(Quizgrade::getCreatedTime).toArray(String[]::new);
                gradeValues = quizgrades.stream().mapToInt(Quizgrade::getScore).toArray();
            } else {
                Log.e(getClass().getName(), "LiveData 오류 발생");
            }
        });

        docsBarChart = binding.docsWordBarChart;
        docsTimeLineChart = binding.docsTimeLineChart;
        docsGradeLineChart = binding.docsGradeLineChart;

        drawBarChart(docsBarChart, wordX, wordValues);
        drawLineChart(docsTimeLineChart, timeX, timeValues);
        drawLineChart(docsGradeLineChart, gradeX, gradeValues);

    }

    private void setBarData(String[] dayX, int[] values) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // 데이터 및 라벨 생성
        for (int i = 0; i < 10; i++) {
            entries.add(new BarEntry(i, values[i]));
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
        XAxis xAxis = docsBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size()); // 모든 라벨 표시

        // 차트에 데이터 설정
        docsBarChart.setData(data);
        docsBarChart.invalidate(); // 차트 갱신
    }

    private void drawBarChart(HorizontalBarChart barChart, String[] dayX, int[] values) {
        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart.setTouchEnabled(false); // 터치 유무
        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart.setExtraOffsets(10f, 0f, 40f, 0f);
        barChart.setFitBars(true);

        setBarData(dayX, values); // 데이터 설정

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
    }

    private void drawLineChart(LineChart lineChart, String[] dayX, int[] values) {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            entries.add(new Entry(i, values[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setColor(Color.parseColor("#E8C1A0")); // 선 색깔 지정
        lineDataSet.setDrawValues(false); // 해당 포인트에 값 라벨 지정
        lineDataSet.setLineWidth(2f); // 선 두깨 지정
        lineDataSet.setCircleRadius(3f); // 포인트 원 크키 지정
        lineDataSet.setCircleColor(Color.parseColor("#E8C1A0")); // 포인트 원 색 지정
        lineDataSet.setDrawCircleHole(false); // 포인트의 원에 구멍 여부 지정

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData); // 그래프에 데이터 설정

        XAxis xAxis = lineChart.getXAxis(); // 그래프의 x축 갖고오기
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayX)); // 그래프의 x축 그래프 생성
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 그래프의 x축 위치 지정 (디폴드: 위)

        YAxis yAxis = lineChart.getAxisLeft(); // 그래프의 y축 갖고오기
        yAxis.setAxisMaximum(40); // y축 최댓값 지정
        yAxis.setAxisMinimum(0); // y축 최솟값 지정
        yAxis.setLabelCount(9, true); // y축 갯수 지정 (값 간격을 조정)


        xAxis.setDrawGridLines(true); // 그래프 그려지는 곳 x축 격자 생성
        yAxis.setDrawGridLines(true); // 그래프 그려지는 곳 y축 격자 생성
        xAxis.setGridLineWidth(0.5f); // x축 격자 두깨
        yAxis.setGridLineWidth(0.5f); // y축 격자 두깨
        yAxis.setXOffset(10f); // 그래프와 y축 간격 조절 (오른쪽으로 이동)

        lineChart.getDescription().setEnabled(false); // 그래프 설명 비활성화
        lineChart.getAxisLeft().setDrawGridLines(true); // y축 격자 생성 가능 여부
        lineChart.getXAxis().setDrawGridLines(true); // x축 격자 생성 가능 여부
        lineChart.getAxisRight().setEnabled(false); // 오른쪽 y축 표시 여부
        lineChart.invalidate(); // 그래프 표시 및 갱신
    }

}