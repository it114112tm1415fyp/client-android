package it114112tm1415fyp.com.client_android;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormator {
    final static SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    final static SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    final static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd");
    final static SimpleDateFormat longTimeFormat = new SimpleDateFormat("HH:mm:ss");
    final static SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    public static String getDate(String date) {
        try {
            return longDateFormat.format(serverFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getShortDate(String date) {
        try {
            return shortDateFormat.format(longDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLongTime(String date) {
        try {
            return longTimeFormat.format(serverFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDateTime(String date) {
        return getDate(date) + " " + getLongTime(date);
    }

    public static String getShortTime(String date) {
        try {
            return shortTimeFormat.format(serverFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
