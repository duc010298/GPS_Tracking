package com.github.duc010298.android.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.android.services.TrackingLocationService;

import static android.content.Context.MODE_PRIVATE;

public class ServicesHelper {
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startTrackingLocationService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isTrackingLocationRun", true);
        edit.apply();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartTracking");
        context.sendBroadcast(broadcastIntent);
    }

    public void stopTrackingLocationService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isTrackingLocationRun", false);
        edit.apply();

        TrackingLocationService trackingLocationService = new TrackingLocationService();
        Intent serviceIntent = new Intent(context.getApplicationContext(), trackingLocationService.getClass());
        if (isMyServiceRunning(trackingLocationService.getClass(), context)) {
            context.stopService(serviceIntent);
        }
    }

    public void startAllServices(Context context) {
        startTrackingLocationService(context);
    }

    public void stopAllServices(Context context) {
        stopTrackingLocationService(context);
    }
}
