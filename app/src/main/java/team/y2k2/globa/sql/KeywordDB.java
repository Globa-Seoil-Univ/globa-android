package team.y2k2.globa.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeywordDB extends SQLiteOpenHelper {
    SQLiteDatabase sqlDB;

    public KeywordDB(Context context) {
        super(context, "keyword", null, 1);
        sqlDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE keyword("
                +"record_id INT PRIMARY KEY,"
                +"keyword VARCHAR(30));";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists keyword";

        db.execSQL(sql);
        onCreate(db);
    }

    public void onInsert(int record_id, String keyword) {
        if (!isKeywordExists(record_id, keyword)) { // Insert only if record_id doesn't exist
            String sql = "INSERT INTO keyword VALUES(" + record_id + ",'" + keyword + "');";
            sqlDB.execSQL(sql);
        }
    }

    private boolean isKeywordExists(int recordId, String keyword) {
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM keyword WHERE record_id = " + recordId +" AND keyword LIKE '"+ keyword +"';", null);

        if(cursor.getCount() == 0)
            return true;
        else
            return false;
    }
}
