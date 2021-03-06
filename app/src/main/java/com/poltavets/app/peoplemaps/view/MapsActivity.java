package com.poltavets.app.peoplemaps.view;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poltavets.app.peoplemaps.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //VIEW
    private GoogleMap mMap; //GOOGLE MAP FRAGMENT-VIEW

    //FIELDS
    private double latitude,longitude;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //CONTENT VIEW

        /*
        INIT TOOLBAR
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_maps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_maps));
        //

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //

        if(getIntent()!=null){ //GET LOCATION INFO
            latitude=getIntent().getDoubleExtra("lat",0.0);
            longitude=getIntent().getDoubleExtra("long",0.0);
            name=getIntent().getStringExtra("name");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng userlocation=new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(userlocation).title("This is "+name+"!  "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
    }
}
