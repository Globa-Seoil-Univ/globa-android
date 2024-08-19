package team.y2k2.globa.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FolderDB extends SQLiteOpenHelper {
    SQLiteDatabase sqlDB;
    Context context;

    public FolderDB(Context context) {
        super(context, "folder", null, 1);
        this.context = context;

        sqlDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE folder("
                +"record_id INT PRIMARY KEY,"
                +"user_id INT,"
                +"title VARCHAR(32),"
                +"datetime VARCHAR(300));";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists folder";

        db.execSQL(sql);
        onCreate(db);
    }

    public void onInsert(int folder_id, String title, String datetime) {
        if (isFolderIdExists(folder_id)) {
            String sql = "INSERT INTO folder VALUES(" + folder_id + ",'" + title + "','" + datetime + "');";
            sqlDB.execSQL(sql);
        }
    }

    private boolean isFolderIdExists(int folderId) {
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM folder WHERE folder_id = " + folderId +";", null);

        if(cursor.getCount() == 0)
            return true;
        else
            return false;
    }
}