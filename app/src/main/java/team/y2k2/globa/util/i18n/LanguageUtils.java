package team.y2k2.globa.util.i18n;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;

public class LanguageUtils {
    public static Locale getCurrentLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLocales().get(0);
    }

    public static String getCurrentLanguageCode(Context context) {
        return getCurrentLocale(context).getLanguage();
    }

    public static String getCurrentCountryCode(Context context) {
        return getCurrentLocale(context).getCountry();
    }

    public static String getCurrentLocaleString(Context context) {
        return getCurrentLocale(context).toString(); // 예: ko_KR, en_US
    }
}