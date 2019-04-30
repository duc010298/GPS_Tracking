package com.github.duc010298.android.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.github.duc010298.android.helper.DatabaseHelper;

public class TrackingLocationService extends Service {

    private LocationManager locationManager;
    private MyLocationListener listener;
    private DatabaseHelper databaseHelper;

    public TrackingLocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseHelper = new DatabaseHelper(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_STICKY;
        }
        //5 minute and 10 meter
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 20, listener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartTracking");
        sendBroadcast(broadcastIntent);

        locationManager.removeUpdates(listener);
    }

    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(final Location loc) {
            databaseHelper.addLocationHistory(loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {

        }


        public void onProviderEnabled(String provider) {

        }
    }
}
