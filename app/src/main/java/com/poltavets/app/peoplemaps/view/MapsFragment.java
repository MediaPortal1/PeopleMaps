package com.poltavets.app.peoplemaps.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poltavets.app.peoplemaps.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    //VIEW
    private GoogleMap mMap; //GOOGLE MAP FRAGMENT-VIEW

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        INFLATE VIEW
         */
        View view=inflater.inflate(R.layout.fragment_map,null);

        /*
        INIT MAPS
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void addMarkerToMap(String name,double latitude,double longitude){
        LatLng userlocation=new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(userlocation).title("This is "+name+"!  "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
    }
}
