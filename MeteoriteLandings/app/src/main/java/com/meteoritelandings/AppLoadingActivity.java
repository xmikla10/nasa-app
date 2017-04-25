package com.meteoritelandings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.meteoritelandings.ExceptionHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Peter Miklánek
 *
 * Activity that simply shows the logo and title, waits for four seconds and starts the MainPage
 */
public class AppLoadingActivity extends Activity {
    public static final int LOADING_TIME_IN_SECONDS = 4;
    public static final String MY_PREFS_NAME = "ControlFile";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_loading);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try
                {
                    super.run();
                    sleep(LOADING_TIME_IN_SECONDS * 500);

                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String control = prefs.getString("control", null);

                    //if ( control == null)
                    //{
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("control", "0");
                        editor.commit();

                        setSynchronization();
                   // }
                }
                catch (Exception e)
                {
                    Log.e("AppLoadingActivity", "Exception: " + e.getMessage());

                }
                finally
                {
                    Intent i = new Intent(AppLoadingActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };

        welcomeThread.start();
    }

    /**
     * Method return actual date and time
     * @return actual date and time
     */
    public String getActualDate()
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        final String currentDateandTime = dateFormat.format(cal.getTime());
        Date datePlus = null;

        try
        {
            datePlus = dateFormat.parse(currentDateandTime);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        cal.setTime(datePlus);

        String date = dateFormat.format(cal.getTime());

        return date;
    }

    /**
     * Method set next synchronization date
     */
    public void setSynchronization()
    {
        String actualDate = getActualDate();

        Integer Hour = Integer.valueOf(actualDate.substring(0, 2));
        Integer Minute = Integer.valueOf(actualDate.substring(3, 5));
        Integer Day = Integer.valueOf(actualDate.substring(6, 8));
        Integer Month = Integer.valueOf(actualDate.substring(9, 11));
        Integer Year = Integer.valueOf(actualDate.substring(12, 16));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month - 1);
        cal.set(Calendar.DAY_OF_MONTH, Day);
        cal.set(Calendar.HOUR_OF_DAY, Hour);
        cal.set(Calendar.MINUTE, Minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.HOUR_OF_DAY, 24);
        //cal.add(Calendar.MINUTE, 1);

        int _id = (int) System.currentTimeMillis();

        Intent intent = new Intent(AppLoadingActivity.this, Synchronization.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pending = PendingIntent.getBroadcast(AppLoadingActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
        cal = null;
    }
}
