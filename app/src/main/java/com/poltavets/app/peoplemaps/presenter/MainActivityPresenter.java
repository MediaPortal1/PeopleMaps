package com.poltavets.app.peoplemaps.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.BaseAdapter;

import com.poltavets.app.peoplemaps.model.FireBaseConnection;
import com.poltavets.app.peoplemaps.view.MainView;
import com.poltavets.app.peoplemaps.view.MapsActivity;


public class MainActivityPresenter implements MainActivityInterface{

    private Context context; //APP CONTEXT
    private MainView view; //MAINVIEW
    private double latitude, longitude; //LOCATIOON INFO
    private FireBaseConnection database; // FIREBASE

    public MainActivityPresenter(Context context, MainView view,String token) {
        /*
        INIT
         */
        this.context = context;
        this.view=view;

        initLocation();//GET CURRENT POSITION

        /*
        CONNECT TO FIREBASE
         */
        database = new FireBaseConnection(context, this, token, latitude, longitude);//
    }

    private void initLocation() { //GET LOCATION
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setLocation(location.getLatitude(),location.getLongitude());
    }

    private void setLocation(double lat,double lon){ //SETTER LOCATION
        this.latitude=lat;
        this.longitude=lon;
    }


    @Override
    public void setAdapter(BaseAdapter adapter) { //SETTER ADAPTER
        view.setAdapter(adapter);
        setListVisible();
    }

    @Override
    public void setListVisible() {
        if(database!=null) {
            if (database.getUserCount() > 0) view.setListEnabled();
        }
    }

    @Override
    public Intent getMapIntent(int position) {
        Intent intent=new Intent(context,MapsActivity.class);
        intent.putExtra("long",database.getLongitude(position));
        intent.putExtra("lat",database.getLatitude(position));
        intent.putExtra("name",database.getName(position));

        return intent;
    }

    @Override
    public void refreshUserList() {
        database.loadUsers();
    }

    @Override
    public void setOnProgress() {
        view.setListOnProgress();
    }

    /*************LOCATION LISTENER*************/
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {setLocation(loc.getLatitude(),loc.getLongitude());}
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
