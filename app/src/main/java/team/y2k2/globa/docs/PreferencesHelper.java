package team.y2k2.globa.docs;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PreferencesHelper {
    private static final String PREF_NAME = "activity_data";
    private static final String DATA_KEY = "data_list";
    private static final int MAX_DATA_COUNT = 10;
    private SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addData(String date, int durationTime) {
        try {
            JSONArray dataArray = getDataArray();
            JSONObject newData = new JSONObject();
            newData.put("date", date);
            newData.put("durationTime", durationTime);

            if (dataArray.length() >= MAX_DATA_COUNT) {
                // 가장 오래된 데이터 제거
                JSONArray newArray = new JSONArray();
                for (int i = 1; i < dataArray.length(); i++) {
                    newArray.put(dataArray.get(i));
                }
                newArray.put(newData);
                dataArray = newArray;
            } else {
                dataArray.put(newData);
            }

            sharedPreferences.edit().putString(DATA_KEY, dataArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getDataArray() {
        String dataString = sharedPreferences.getString(DATA_KEY, "[]");
        try {
            return new JSONArray(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

}
