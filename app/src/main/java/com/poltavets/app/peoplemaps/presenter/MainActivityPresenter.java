package com.poltavets.app.peoplemaps.presenter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.poltavets.app.peoplemaps.model.FireBaseConnection;
import com.poltavets.app.peoplemaps.view.MainView;
import com.poltavets.app.peoplemaps.view.MapsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivityPresenter implements MainActivityInterface{

    private Context context;
    private MainView view;
    private double latitude, longitude;
    private FireBaseConnection database;
    private final String TAG="MainActivityPresenter";


    @TargetApi(Build.VERSION_CODES.M)
    public MainActivityPresenter(Context context, MainView view,String token) {
        this.context = context;
        this.view=view;
        initLocation();
        database = new FireBaseConnection(context, this, token, latitude, longitude);//
    }

    private void initLocation() {
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

    private void setLocation(double lat,double lon){
        this.latitude=lat;
        this.longitude=lon;
    }


    @Override
    public void setAdapter(BaseAdapter adapter) {
        view.setAdapter(adapter);
        updateList();
    }

    @Override
    public void updateList() {
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
    public void setOnProgress() {
        view.setListOnProgress();
    }

    /*************LOCATION LISTENER*************/
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            setLocation(loc.getLatitude(),loc.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }
}
