package team.y2k2.globa.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import team.y2k2.globa.api.model.entity.Keyword;

public class RecordDB extends SQLiteOpenHelper {
    SQLiteDatabase sqlDB;
    Context context;

    public RecordDB(Context context) {
        super(context, "record", null, 1);
        this.context = context;

        sqlDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE record("
                +"record_id INT PRIMARY KEY,"
                +"folder_id INT,"
                +"title VARCHAR(32),"
                +"datetime VARCHAR(300));";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists record";

        db.execSQL(sql);
        onCreate(db);
    }

    public void onInsert(int record_id, int folder_id, String title, String datetime, List<Keyword> keywords) {
        if (isRecordIdExists(record_id)) {
            String sql = "INSERT INTO Record VALUES(" + record_id + "," + folder_id + ",'" + title + "','" + datetime + "');";
            KeywordDB keywordDB = new KeywordDB(context);

            for(int i = 0; i < keywords.size(); i++) {
                Keyword keyword = keywords.get(i);
                keywordDB.onInsert(record_id, keyword.getWord());
            }

            sqlDB.execSQL(sql);
        }
    }

    private boolean isRecordIdExists(int recordId) {
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM Record WHERE record_id = " + recordId +";", null);

        if(cursor.getCount() == 0)
            return true;
        else
            return false;
    }
}
