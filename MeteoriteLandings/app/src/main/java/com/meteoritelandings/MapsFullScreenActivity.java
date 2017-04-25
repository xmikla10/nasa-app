package com.meteoritelandings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Peter Miklánek
 *
 * Class represent fullscreen map started by FAB in MainActivity
 */

public class MapsFullScreenActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    String[] myStrings;
    Realm realm;
    public ArrayList<MeteorsAd> a =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_map);

        a.clear();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_full_screen);
        mapFragment.getMapAsync(MapsFullScreenActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent( MapsFullScreenActivity.this, MainActivity.class);
            realm.close();
            startActivity(intent);
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        realm = Realm.getInstance(MapsFullScreenActivity.this);

        RealmResults<Meteors> results = realm.where(Meteors.class).findAll();
        int tmp = 0;
        myStrings = new String[results.size()];

        for(Meteors x : results)
        {
            LatLng meteor = new LatLng( Double.valueOf(x.getCoordinateA()), Double.valueOf(x.getCoordinateB()));
            mMap.addMarker(new MarkerOptions().position(meteor).title(x.getName()));
            myStrings[tmp] = x.getId();
            tmp++;
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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                String id = marker.getId().toString();
                id = id.substring(1, id.length());
                Integer value = Integer.valueOf(id);
                Iterator<MeteorsAd> iterator = a.iterator();

                for (int i = 0; i < a.size(); i++)
                {
                    if( a.get(i).getId().equals(myStrings[value].toString()))
                    {
                        Intent intent = new Intent( MapsFullScreenActivity.this, MapsActivity.class);

                        intent.putExtra("name", a.get(i).getName());
                        intent.putExtra("year", a.get(i).getYear());
                        intent.putExtra("id", a.get(i).getId());
                        intent.putExtra("mass", a.get(i).getMass());
                        intent.putExtra("type", a.get(i).getType());
                        intent.putExtra("nametype", a.get(i).getNametype());
                        intent.putExtra("recclass", a.get(i).getRecclass());
                        intent.putExtra("fall", a.get(i).getFall());
                        intent.putExtra("reclat", a.get(i).getReclat());
                        intent.putExtra("reclong", a.get(i).getReclong());
                        intent.putExtra("coordinateA", a.get(i).getCoordinateA());
                        intent.putExtra("coordinateB", a.get(i).getCoordinateB());

                        startActivity(intent);
                        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                        break;
                    }
                }
            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        realm.close();
    }
}
