package com.meteoritelandings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;


/**
 * Created by Peter Miklánek
 *
 * Class represent BroadcastReceiver for automatic actualization every day
 */
public class Synchronization extends WakefulBroadcastReceiver
{
    public static final String MY_PREFS_NAME = "ControlFile";
    public Realm realm;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        if ( isOnline(context) == true)
        {
            synchronization(context);
            sendNotification( context.getString(R.string.up_to_date), context);
            setSynchronization("24", context);
        }
        else
        {
            setSynchronization("1", context);
        }

    }


    /**
     * Method control internet connection
     * @param context Context
     * @return true if is connection, false if not
     */
    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        else
        {
            return false;
        }
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
     * @param value hours
     * @param context Context
     */
    public void setSynchronization(String value, Context context)
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

        cal.add(Calendar.HOUR_OF_DAY, Integer.valueOf(value));
        //cal.add(Calendar.MINUTE, Integer.valueOf(value));

        int _id = (int) System.currentTimeMillis();

        Intent intent = new Intent(context, Synchronization.class);

        PendingIntent pending = PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
        cal = null;
    }

    /**
     * Method send notification when synchronization is done
     * @param notificaton_message message for notification
     * @param context Context
     */
    public void sendNotification(String notificaton_message, Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = {0, 200};

        Notification notif = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setContentTitle("Meteorite Landings")
                .setContentText(notificaton_message)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notif.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify( 1, notif);

    }


    Context contextP;

    /**
     * Method parse JSON from HttpHandler and save to realm db
     * @param context Context
     */
    public void synchronization(Context context)
    {
        contextP = context;
        if (isOnline(context))
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        HttpHandler sh = new HttpHandler();
                        String url = contextP.getString(R.string.nasa_url);
                        String jsonStr = sh.makeServiceCall(url);

                        jsonStr = "{" + "meteors:" + jsonStr + "}";

                        if (jsonStr != null)
                        {
                            try
                            {
                                realm = Realm.getInstance(contextP);
                                JSONObject jsonObj = new JSONObject(jsonStr);
                                JSONArray meteors = jsonObj.getJSONArray("meteors");

                                for (int i = 0; i < meteors.length(); i++)
                                {
                                    Meteors meteor = new Meteors();
                                    JSONObject c = meteors.getJSONObject(i);
                                    String year = ifValueExist(c, "year");

                                    if (!year.equals("") )
                                    {
                                        year = year.substring(0, 4);
                                        Integer y = Integer.valueOf(year);
                                        if ( y >= 2011)
                                        {
                                            if (realm.where(Meteors.class).equalTo("id", ifValueExist(c, "id")).count() == 0)
                                            {
                                                //save new to realm db
                                                parseJSON(year, meteor, c);
                                            }
                                        }
                                    }
                                }
                                realm.close();
                            }
                            catch (JSONException e)
                            {
                                Log.e(TAG, "Exception: " + e.getMessage());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Method control value in json
     * @param json json object
     * @param str string to control
     * @return string
     */
    public String ifValueExist(JSONObject json, String str)
    {
        try
        {
            return json.getString(str);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * Method parse json and save to realm db
     * @param year meteor year
     * @param meteor object
     * @param c json object
     */
    public void parseJSON(String year, Meteors meteor, JSONObject c)
    {
        meteor.setYear(year);
        try
        {
            JSONObject geolocation = c.getJSONObject("geolocation");
            meteor.setType(ifValueExist(geolocation, "type"));
            JSONArray coordinates = geolocation.getJSONArray("coordinates");
            meteor.setCoordinateA(coordinates.get(0).toString());
            meteor.setCoordinateB(coordinates.get(1).toString());
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        meteor.setId(ifValueExist(c, "id"));

        meteor.setName(ifValueExist(c, "name"));

        meteor.setMass(ifValueExist(c, "mass"));

        meteor.setNametype(ifValueExist(c, "nametype"));

        meteor.setRecclass(ifValueExist(c, "recclass"));

        meteor.setReclat(ifValueExist(c, "reclat"));

        meteor.setReclong(ifValueExist(c, "reclong"));

        meteor.setFall(ifValueExist(c, "fall"));

        realm.beginTransaction();
        Meteors a = realm.copyToRealm(meteor);
        realm.commitTransaction();
    }
}
