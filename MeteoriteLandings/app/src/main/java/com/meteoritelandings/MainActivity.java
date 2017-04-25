package com.meteoritelandings;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Peter Miklánek
 *
 * Class represent main activity of application
 */

public class MainActivity extends AppCompatActivity
{

    public String sURL;
    public ArrayList<MeteorsAd> a =new ArrayList<>();
    public CustomAdapterMeteors adapter;
    public ProgressBar bar;
    private ProgressDialog progressDialog;
    public Realm realm;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MY_PREFS_NAME = "ControlFile";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent( MainActivity.this, MapsFullScreenActivity.class);
                realm.close();
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
            }
        });

        sURL = getString(R.string.nasa_url);
        a.clear();


        //realm = Realm.getInstance(MainActivity.this);
        //realm.close();
        //Realm.deleteRealm(realm.getConfiguration());

        bar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        bar.setVisibility(View.VISIBLE);

        realm = Realm.getInstance(MainActivity.this);
        RealmResults<Meteors> results = realm.where(Meteors.class).findAll();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String control = prefs.getString("control", null);

                if ( control.equals("1"))
                {
                    showList();
                    setPref("0");
                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

        String control = prefs.getString("control", null);

        if( results.size() != 0)
        {
            realm.close();
            showList();
        }
        else
        {
            realm.close();
            downloadAndSave();
        }

        if ( control.equals("1"))
        {
            showList();
            setPref("0");
        }
    }

    /**
     * Set SharedPreferences
     * @param number value to set
     */
    public void setPref(String number)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("control", number);
        editor.commit();
    }

    /**
     * Method sort meteors and show in listview
     */
    public void showList()
    {
        realm = Realm.getInstance(MainActivity.this);
        RealmResults<Meteors> results = realm.where(Meteors.class).findAll();

        for(Meteors x : results)
        {
            MeteorsAd m = new MeteorsAd();
            m.setName(x.getName());
            m.setId(x.getId());
            m.setYear(x.getYear());
            m.setMass(x.getMass());
            m.setNametype(x.getNametype());
            m.setFall(x.getFall());
            m.setType(x.getType());
            m.setReclong(x.getReclong());
            m.setReclat(x.getReclat());
            m.setRecclass(x.getRecclass());
            m.setCoordinateA(x.getCoordinateA());
            m.setCoordinateB(x.getCoordinateB());
            a.add(m);
        }
        realm.close();

        ArrayList<MeteorsAd> sortedByMass = new ArrayList<MeteorsAd>(a);

        Collections.sort(sortedByMass, new Comparator<MeteorsAd>()
        {
            public int compare(MeteorsAd m1, MeteorsAd m2)
            {
                return Double.valueOf(m2.getMass()).compareTo(Double.valueOf(m1.getMass()));
            }
        });

        adapter = new CustomAdapterMeteors(MainActivity.this, sortedByMass);
        ListView lv = (ListView) findViewById(R.id.listViewMeteors);
        bar.setVisibility(View.GONE);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final  MeteorsAd m = (MeteorsAd) adapter.getItem(position);
                Intent intent = new Intent( MainActivity.this, MapsActivity.class);

                intent.putExtra("name", m.getName());
                intent.putExtra("year", m.getYear());
                intent.putExtra("id", m.getId());
                intent.putExtra("mass", m.getMass());
                intent.putExtra("type", m.getType());
                intent.putExtra("nametype", m.getNametype());
                intent.putExtra("recclass", m.getRecclass());
                intent.putExtra("fall", m.getFall());
                intent.putExtra("reclat", m.getReclat());
                intent.putExtra("reclong", m.getReclong());

                intent.putExtra("coordinateA", m.getCoordinateA());
                intent.putExtra("coordinateB", m.getCoordinateB());

                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);

            }
        });
    }

    /**
     * Method download .json and parse it to realm db
     */
    public void downloadAndSave()
    {
        if (isOnline())
        {
            realm = Realm.getInstance(MainActivity.this);
            realm.close();
            Realm.deleteRealmFile(MainActivity.this);

            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        HttpHandler sh = new HttpHandler();
                        String url = sURL;
                        String jsonStr = sh.makeServiceCall(url);

                        jsonStr = "{" + "meteors:" + jsonStr + "}";

                        if (jsonStr != null)
                        {
                            try
                            {
                                realm = Realm.getInstance(MainActivity.this);
                                JSONObject jsonObj = new JSONObject(jsonStr);
                                JSONArray meteors = jsonObj.getJSONArray("meteors");

                                for (int i = 0; i < meteors.length(); i++)
                                {
                                    Meteors meteor = new Meteors();
                                    JSONObject c = meteors.getJSONObject(i);
                                    String year = ifValueExist(c, "year");

                                    if (!year.equals(""))
                                    {
                                        year = year.substring(0, 4);
                                        Integer y = Integer.valueOf(year);

                                        if (y >= 2011)
                                        {
                                            parseJSON(year, meteor, c);
                                        }
                                    }
                                }

                                realm.close();
                                runUiThread();
                            }
                            catch (JSONException e)
                            {
                                Log.e(TAG, "JSONException: " + e.getMessage());
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
        else
        {
            Toast.makeText(this,  R.string.toast_first_start_no_internet ,Toast.LENGTH_LONG).show();
            bar.setVisibility(View.GONE);

        }
    }

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

    /**
     * Method actualizate realm db
     */
    public void synchronization()
    {
        if (isOnline())
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        HttpHandler sh = new HttpHandler();
                        String url = sURL;
                        String jsonStr = sh.makeServiceCall(url);

                        jsonStr = "{" + "meteors:" + jsonStr + "}";

                        if (jsonStr != null)
                        {
                            try
                            {
                                realm = Realm.getInstance(MainActivity.this);
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
                                a.clear();
                                realm.close();
                                runUiThread();
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
            try
            {
                thread.join();
                Toast.makeText(this,  getString(R.string.up_to_date) ,Toast.LENGTH_LONG).show();

            }
            catch (Exception e )
            {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        else
        {
            Toast.makeText(this, R.string.toast_not_internet,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method run on ui thread
     */
    public void runUiThread()
    {
        runOnUiThread(new Runnable(){
            @Override
            public void run()
            {
                showList();
            }
        });
    }

    /**
     * Method control if string contains in json object
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onDestroy()
    {
        super.onDestroy();

    }

    protected void onPause()
    {
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
    }

    protected void onStop()
    {
        super.onStop();
    }

    protected void onStart()
    {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String control = prefs.getString("control", null);

        if ( control.equals("1"))
        {
            showList();
            setPref("0");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        if (id == R.id.action_synch)
        {
            synchronization();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method control internet connection
     */
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected())
        {
            return true;
        }
        else
        {
            Log.e(TAG, "Exception: " + "internet connection error");
            return false;
        }
    }

    /**
     * Method for actual date and time
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
}
