package team.y2k2.globa.util.i18n;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {
    public static String getDateFormat(Context context, String datetime) {
        Locale currentLocale = LanguageUtils.getCurrentLocale(context);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDateFormat outputFormat = new SimpleDateFormat(getDateFormatPattern(currentLocale), currentLocale);

        Date date;
        String outputDate;

        try {
            date = inputFormat.parse(datetime);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }

        return outputDate;
    }

    public static String getTimeFormat(int milliseconds) {
        milliseconds /= 1000;
        int hours = milliseconds / 3600;
        milliseconds %= 3600;
        int minutes = milliseconds / 60;
        int second = milliseconds % 60;
        if (hours > 0) return String.format(Locale.KOREA, "%02d:%02d:%02d", hours, minutes, second);
        else return String.format(Locale.KOREA, "%02d:%02d", minutes, second);
    }

    public static String getDateFormatPattern(Locale locale) {
        if (locale.equals(Locale.KOREA)) {
            return "yyyy년 MM월 dd일 HH:mm:ss";
        } else if (locale.equals(Locale.JAPAN)) {
            return "yyyy/MM/dd HH:mm:ss";
        } else if (locale.equals(Locale.US)) {
            return "MM/dd/yyyy HH:mm:ss";
        } else {
            return "yyyy/MM/dd HH:mm:ss";
        }
    }

    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(getDateFormatPattern(locale), locale);
    }
}