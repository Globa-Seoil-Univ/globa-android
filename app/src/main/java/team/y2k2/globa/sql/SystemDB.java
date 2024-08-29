package team.y2k2.globa.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import team.y2k2.globa.R;
import team.y2k2.globa.main.profile.alert.AlertActivity;

public class SystemDB extends SQLiteOpenHelper {
    SQLiteDatabase sqlDB;
    public SystemDB(Context context) {
        super(context, "system", null, 1);
        sqlDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE system("
                +"systemName VARCHAR(30) PRIMARY KEY,"
                +"systemClassName VARCHAR(50));";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists system";

        db.execSQL(sql);
        onCreate(db);
    }

    public void onInsert(int icon, String systemName, String systemClassName) {
        if (isSystemExists(systemName)) {
            String sql = "INSERT INTO system VALUES(" + systemName + "," + systemClassName + ");";
            sqlDB.execSQL(sql);
        }
    }

    public void onDefault(){
        String systemsSQL[] = { "INSERT INTO system VALUES(" + R.string.profile_alert_setting + "," + new AlertActivity().getClass().getName() + ");" };
    }

    private boolean isSystemExists(String systemName) {
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM system WHERE systemName LIKE '"+ systemName +"';", null);

        if(cursor.getCount() == 0)
            return true;
        else
            return false;
    }
}
