package it.reply.timbrature.timbrature.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;

/**
 //  Utils
 //  Timbrature
 //
 //  Created by Raffaele Forgione @ Syskoplan Reply.
 //  Copyright © 2017 Acea. All rights reserved.
 */

public class Utils {

    public static String loadJSONFromAsset(String filename, Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String oDataDateToDate(String odataDate) {
        if (odataDate == null || odataDate == "") {
            return null;
        }
        String dt = oDataLongFormatString(odataDate);
        Date dateValue = new Date(Long.valueOf(dt));
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ITALIAN);
        return df.format(dateValue);
    }

    private static String oDataLongFormatString(String odataDate){
        return odataDate.replace("/Date(", "").replace(")/","");
    }

    public static Long dateInMillis(String odataDate){
        if(odataDate.length() == 13){
            return Long.valueOf(odataDate);
        }
        else{
            return Long.valueOf(odataDate.replace("/Date(", "").replace(")/",""));
        }
    }

    public static String formatLtime(String ltime){
        if(ltime.length() == 8){
            return ltime;
        }
        else {
            return (((ltime.replace("PT", "")).replace("H", ":")).replace("M", ":")).replace("S", "");
        }
    }

    public static Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String backToLTime(String time){
        if(time == null || time.length() == 0){
            return "";
        }
        else{
            String[] times = time.split(":");

            String hour = times[0];
            if(hour.length() == 1){
                hour = "0" + hour;
            }

            String minute = times[1];

            String second = "00";

            return "PT" + hour + "H" + minute + "M" + second + "S";
        }

    }

    public static String backToLDate(String date){

        DateFormat dfm = new SimpleDateFormat("ddMMyyyy", Locale.ITALIAN);
        dfm.setTimeZone(TimeZone.getTimeZone("UTC+2"));//Specify your timezone
        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(date).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return "/Date(" + String.valueOf(unixtime) + ")/";
    }


    public static void showStatusMessage(String status, Context context){
        CharSequence text = status;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public static void showStatusMessage(String title, String subtitle, int textColor, int bgColor, Drawable icon, int iconBG, Context context){
        AchievementUnlocked achievementUnlocked = new AchievementUnlocked(context);
        achievementUnlocked.setRounded(false).setLarge(true).setTopAligned(false).setDismissible(true);
        AchievementData data = new AchievementData();
        data.setTitle(title);
        data.setSubtitle(subtitle);
        data.setIcon(icon);
        data.setTextColor(textColor);
        data.setIconBackgroundColor(iconBG);
        data.setBackgroundColor(bgColor);
        data.setPopUpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        achievementUnlocked.show(data);

    }

    public static boolean isNullOrEmpty(String string){
        if(string == null || string.length() == 0){
            return  true;
        }
        else return false;
    }

    public static String getXSmpAppcId(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("xsmpappcid", null);
    }

    public static String getUsername(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("password", null);
    }

    public static String getPassword(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("password", null);
    }

    public static String addMinutesToTime(int minutes, String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millisecs = minutes * 60 * 1000;

        Date resultdate = new Date(c.getTimeInMillis() + millisecs);
        return sdf.format(resultdate);
    }

    public static String subtractMinutesToTime(int minutes, String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millisecs = minutes * 60 * 1000;

        Date resultdate = new Date(c.getTimeInMillis() - millisecs);
        return sdf.format(resultdate);
    }

    public static String subtractMinutesToTime(int minutes, Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long millisecs = minutes * 60 * 1000;

        Date resultdate = new Date(c.getTimeInMillis() - millisecs);
        return sdf.format(resultdate);
    }

    public static void validateUrl(String url) throws SmpExceptionInvalidInput {
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_MALFORMED_SMP_SERVICE_URL);
        }
    }

    public static String dayOfWeek(int dayOfWeek){
        String weekDay = "";

        switch (dayOfWeek){
            case Calendar.MONDAY:
                weekDay = "Lunedì";
                break;
            case Calendar.TUESDAY:
                weekDay = "Martedì";
                break;
            case Calendar.WEDNESDAY:
                weekDay = "Mercoledì";
                break;
            case Calendar.THURSDAY:
                weekDay = "Giovedì";
                break;
            case Calendar.FRIDAY:
                weekDay = "Venerdì";
                break;
            case Calendar.SATURDAY:
                weekDay = "Sabato";
                break;
            case Calendar.SUNDAY:
                weekDay = "Domenica";
                break;

        }
        return weekDay;
    }

    public static String monthOfYear(int monthOfYear){
        String monthYear = "";

        switch (monthOfYear){
            case Calendar.JANUARY:
                monthYear = "Gennaio";
                break;
            case Calendar.FEBRUARY:
                monthYear = "Febbraio";
                break;
            case Calendar.MARCH:
                monthYear = "Marzo";
                break;
            case Calendar.APRIL:
                monthYear = "Aprile";
                break;
            case Calendar.MAY:
                monthYear = "Maggio";
                break;
            case Calendar.JUNE:
                monthYear = "Giugno";
                break;
            case Calendar.JULY:
                monthYear = "Luglio";
                break;
            case Calendar.AUGUST:
                monthYear = "Agosto";
                break;
            case Calendar.SEPTEMBER:
                monthYear = "Settembre";
                break;
            case Calendar.OCTOBER:
                monthYear = "Ottobre";
                break;
            case Calendar.NOVEMBER:
                monthYear = "Novembre";
                break;
            case Calendar.DECEMBER:
                monthYear = "Dicembre";
                break;

        }
        return monthYear;
    }

    public static String monthOfYearForShort(int monthOfYear) {
        String month = monthOfYear(monthOfYear);
        if(month.length() > 0){
            return month.substring(0, 3);
        }
        else{
            return "";
        }
    }

}
