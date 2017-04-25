package com.meteoritelandings;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Peter Miklánek
 *
 * Class represent Meteor details with map
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private MapView mapView;
    public String coordinateA;
    public String coordinateB;
    public String meteor_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteor);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            TextView name = (TextView) findViewById(R.id.textViewName);
            TextView year = (TextView) findViewById(R.id.textViewYear);
            TextView mass = (TextView) findViewById(R.id.textViewMass);
            TextView id = (TextView) findViewById(R.id.textViewID);
            TextView nametype = (TextView) findViewById(R.id.textViewNameType);
            TextView reclat = (TextView) findViewById(R.id.textViewReclat);
            TextView reclong = (TextView) findViewById(R.id.textViewReclong);
            TextView recclass = (TextView) findViewById(R.id.textViewRecclass);
            TextView fall = (TextView) findViewById(R.id.textViewFall);
            TextView type = (TextView) findViewById(R.id.textViewType);

            TextView corA = (TextView) findViewById(R.id.textViewCoordinationA);
            TextView corB = (TextView) findViewById(R.id.textViewCoordinationB);

            name.setText(extras.getString("name"));
            year.setText(extras.getString("year"));
            mass.setText(extras.getString("mass") + getString(R.string.grams));
            id.setText(extras.getString("id"));
            nametype.setText(extras.getString("nametype"));
            reclat.setText(extras.getString("reclat"));
            reclong.setText(extras.getString("reclong"));
            recclass.setText(extras.getString("recclass"));
            fall.setText(extras.getString("fall"));
            type.setText(extras.getString("type"));

            coordinateA = extras.getString("coordinateA");
            coordinateB = extras.getString("coordinateB");
            corA.setText(coordinateA);
            corB.setText(coordinateB);


            meteor_name = extras.getString("name");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng meteor = new LatLng( Double.valueOf(coordinateA), Double.valueOf(coordinateB));
        mMap.addMarker(new MarkerOptions().position(meteor).title(meteor_name));

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(meteor, 4);
        mMap.moveCamera(cameraPosition);
        mMap.animateCamera(cameraPosition);
    }
}
