package team.y2k2.globa.main.main;

import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;
import team.y2k2.globa.api.model.entity.Record;

public class MainFragmentModel {

    public static final int RECORDS_FILTER_CURRENTLY = 0;
    public static final int RECORDS_FILTER_MOST_VIEWED = 1;
    public static final int RECORDS_FILTER_SHARED = 2;
    public static final int RECORDS_FILTER_RECEIVED = 3;


    List<Record> records;

    public MainFragmentModel(List<Record> records) {
        this.records = records;
    }


}
