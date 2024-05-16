package team.y2k2.globa.api;

import android.app.Activity;
import android.content.SharedPreferences;

public class ApiClient extends Activity {

    public ApiClient() {
        SharedPreferences preferences = this.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String accessToken = "Bearer " + preferences.getString("accessToken", "");
    }
}
