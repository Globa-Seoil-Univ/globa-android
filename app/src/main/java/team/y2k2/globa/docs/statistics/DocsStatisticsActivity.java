package team.y2k2.globa.docs.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

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
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Quizgrade;
import team.y2k2.globa.api.model.entity.Studytime;
import team.y2k2.globa.databinding.ActivityDocsStatisticsBinding;
import team.y2k2.globa.docs.PreferencesHelper;

public class DocsStatisticsActivity extends AppCompatActivity {

    String[] wordX, timeX, gradeX;
    int[] wordValues, timeValues, gradeValues;
    double[] doubleWordValues, doubleGradeValues;
    ActivityDocsStatisticsBinding binding;
    private HorizontalBarChart docsBarChart;
    private LineChart docsTimeLineChart, docsGradeLineChart;
    DocsStatisticsViewModel docsStatisticsViewModel;
    String folderId, recordId;
    private List<Keyword> keywords;
    private List<Studytime> studytimes;
    private List<Quizgrade> quizgrades;
    private int maxStudyTime;
    private double maxWordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeUI();

    }

    private void initializeUI() {

        // 뒤로가기 버튼
        binding.buttonDocsStatisticsBack.setOnClickListener(v -> {
            finish();
        });

        // API Request를 통한 데이터 수집
        requestData();

        // API Response 데이터를 LiveData로 받아 관찰 및 차트 그리기 실행
        receiveLiveData();

    }

    private void requestData() {
        folderId = getIntent().getStringExtra("folderId");
        recordId = getIntent().getStringExtra("recordId");

        docsBarChart = binding.docsWordBarChart;
        docsTimeLineChart = binding.docsTimeLineChart;
        docsGradeLineChart = binding.docsGradeLineChart;

        docsStatisticsViewModel = new ViewModelProvider(this).get(DocsStatisticsViewModel.class);
        docsStatisticsViewModel.getDocsStatistics(folderId, recordId);
    }

    private void receiveLiveData() {
        // 단어, 퀴즈 점수는 데이터베이스에서 데이터를 받아와 차트 그림 (공부 시간은 일단 주석 처리)
        docsStatisticsViewModel.getDocsStatisticsLiveData().observe(this, docsStatistics -> {
            if(docsStatistics != null) {
                keywords = docsStatistics.getKeywords();
                studytimes = docsStatistics.getStudyTimes();
                quizgrades = docsStatistics.getQuizGrades();

                // 단어 중요도 차트 그리기
                drawKeywordsChart();

                // 공부 시간 차트 그리기
                drawStudyTimeChart();

                // 퀴즈 점수 차트 그리기
                drawQuizGradeChart();

            } else {
                Log.e(getClass().getName(), "LiveData 오류 발생");
            }
        });
    }

    private void drawKeywordsChart() {
        // 단어 중요도 차트
        wordX = keywords.stream().map(Keyword::getWord).toArray(String[]::new);
        doubleWordValues = keywords.stream().mapToDouble(Keyword::getImportance).toArray();

        maxWordValue = 0;
        if(doubleWordValues.length != 0) {
            for(int i = 0; i < doubleWordValues.length; i++) {
                if(doubleWordValues[i] > maxWordValue) {
                    maxWordValue = doubleWordValues[i];
                }
            }
        }

        for(int i = 0; i < doubleWordValues.length; i++) {
            doubleWordValues[i] = doubleWordValues[i] * 100;
        }

        if(wordX.length == 0) {
            String[] newWordX = new String[10];
            double[] newWordValue = new double[10];
            for(int i = 0; i < 10; i++) {
                newWordX[i] = " ";
                newWordValue[i] = 0;
            }
            drawBarChart(docsBarChart, newWordX, newWordValue);
        } else {
            drawBarChart(docsBarChart, wordX, doubleWordValues);
        }
    }

    private void drawStudyTimeChart() {
        // 공부 시간 차트
        timeX = studytimes.stream().map(Studytime::getCreatedTime).toArray(String[]::new);
        timeValues = studytimes.stream().mapToInt(Studytime::getStudyTime).toArray();

        maxStudyTime = 0;
        if(timeValues.length != 0) {
            for(int i = 0; i < timeValues.length; i++) {
                if(timeValues[i] > maxStudyTime) {
                    maxStudyTime = timeValues[i];
                }
            }
        }

        for(int i = 0; i < timeX.length; i++) {
            timeX[i] = timeX[i].substring(0, 10);
        }

        if(timeX.length < 10 && timeX.length > 0) {
            List<String> timeXList = new ArrayList<>();
            List<Integer> timeValuesList = new ArrayList<>();
            for(int i = 0; i < timeX.length; i ++) {
                timeXList.add(timeX[i]);
                timeValuesList.add(timeValues[i]);
            }
            for(int i = 0; i < 10 - timeX.length; i++) {
                timeXList.add("0");
                timeValuesList.add(0);
            }
            String[] newTimeX = new String[10];
            int[] newTimeValues = new int[10];
            int i = 0, j = 0;
            for(String s : timeXList) {
                newTimeX[i] = s;
                i++;
            }
            for(int n : timeValuesList) {
                newTimeValues[j] = n;
                j++;
            }
            drawLineChart(docsTimeLineChart, maxStudyTime, newTimeX, newTimeValues, "공부시간");
        } else if (timeX.length == 0) {
            String[] newTimeX = new String[10];
            int[] newTimeValues = new int[10];
            for(int i = 0; i < 10; i++) {
                newTimeX[i] = "0";
                newTimeValues[i] = 0;
            }
            drawLineChart(docsTimeLineChart, maxStudyTime, newTimeX, newTimeValues, "공부시간");
        } else {
            drawLineChart(docsTimeLineChart, maxStudyTime, timeX, timeValues, "공부시간");
        }
    }

    private void drawQuizGradeChart() {
        // 퀴즈 성적 차트
        gradeX = quizgrades.stream().map(Quizgrade::getCreatedTime).toArray(String[]::new);
        doubleGradeValues = quizgrades.stream().mapToDouble(Quizgrade::getScore).toArray();
        gradeValues = DoubleStream.of(doubleGradeValues).mapToInt(value -> (int)value).toArray();

        if(gradeX.length < 10 && gradeX.length > 0) {
            List<String> gradeXList = new ArrayList<>();
            List<Integer> gradeValuesList = new ArrayList<>();
            for(int i = 0; i < gradeX.length; i ++) {
                gradeXList.add(gradeX[i]);
                Log.d("퀴즈 점수", "퀴즈 점수 들: " + gradeValues[i]);
                gradeValuesList.add(gradeValues[i]);
            }
            for(int i = 0; i < 10 - gradeX.length; i++) {
                gradeXList.add("0");
                gradeValuesList.add(0);
            }
            String[] newGradeX = new String[10];
            int[] newGradeValues = new int[10];
            int i = 0, j = 0;
            for(String s : gradeXList) {
                newGradeX[i] = s;
                i++;
            }
            for(int n : gradeValuesList) {
                newGradeValues[j] = n;
                j++;
            }
            drawLineChart(docsGradeLineChart, 100, newGradeX, newGradeValues, "퀴즈 점수");
        } else if (gradeX.length == 0) {
            String[] newGradeX = new String[10];
            int[] newGradeValues = new int[10];
            for(int i = 0; i < 10; i++) {
                newGradeX[i] = "0";
                newGradeValues[i] = 0;
            }
            drawLineChart(docsGradeLineChart, 100, newGradeX, newGradeValues, "퀴즈 점수");
        } else {
            drawLineChart(docsGradeLineChart, 100, gradeX, gradeValues, "퀴즈 점수");
        }
    }

    private void setBarData(String[] dayX, double[] values) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        ArrayList<Pair<Double, String>> sortedList = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            sortedList.add(new Pair<>(values[i], dayX[i]));
        }

        // 내림차순으로 정렬 (values를 기준으로)
        Collections.sort(sortedList, (o1, o2) -> Double.compare(o1.first, o2.first));

        // 정렬된 값을 entries와 labels에 추가
        for (int i = 0; i < sortedList.size(); i++) {
            entries.add(new BarEntry(i, sortedList.get(i).first.floatValue()));
            labels.add(sortedList.get(i).second); // dayX도 정렬된 값에 맞춤
        }

        // 데이터셋 생성
        BarDataSet dataSet = new BarDataSet(entries, "Data");

        // 각 막대의 색상 설정
        int[] barColors = new int[]{Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#E8C1A0"), Color.parseColor("#B4622F")};
        dataSet.setColors(barColors);
        dataSet.setValueTextSize(10f);

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

    private void drawBarChart(HorizontalBarChart barChart, String[] dayX, double[] values) {
        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart.setTouchEnabled(false); // 터치 유무
        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
        barChart.setExtraOffsets(10f, 0f, 40f, 0f);
        barChart.setFitBars(true);

        List<String> list = Arrays.asList(dayX);
        Collections.reverse(list);

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
        axisLeft.setAxisMaximum((float) (maxWordValue * 100)); // 최댓값
        axisLeft.setGranularity(0.1f); // 기준선 간격 설정
        axisLeft.setDrawLabels(false); // label 삭제

        // 기준선에 원하는 위치의 값을 추가합니다.
        axisLeft.setDrawLabels(true);
        axisLeft.setLabelCount(11, true); // 기준선의 개수를 설정합니다.
        axisLeft.setAxisLineColor(Color.parseColor("#FFFFFFFF")); // 축선 색상 설정
        axisLeft.setAxisLineWidth(2f); // 축선의 두께를 설정합니다.
        axisLeft.setGridColor(Color.parseColor("#DDDDDD")); // 기준선 색상 설정
        axisLeft.setDrawAxisLine(true); // 축선을 그립니다.
        axisLeft.setGridLineWidth(2f);
        axisLeft.setDrawGridLines(true); // 기준선을 그립니다.

        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayX));
    }

    private void drawLineChart(LineChart lineChart, int maxY, String[] dayX, int[] values, String title) {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            entries.add(new Entry(i, values[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, title);
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
        yAxis.setAxisMaximum(maxY); // y축 최댓값 지정
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