package com.github.duc010298.gpstracking.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.gpstracking.helper.ServicesHelper;
import com.github.duc010298.gpstracking.services.TrackingLocationService;

import static android.content.Context.MODE_PRIVATE;

public class RestartTrackingLocation extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        if(pre.getBoolean("isTrackingLocationRun", false)) {
            TrackingLocationService trackingLocationService = new TrackingLocationService();
            Intent serviceIntent = new Intent(context.getApplicationContext(), trackingLocationService.getClass());
            if (!ServicesHelper.isMyServiceRunning(trackingLocationService.getClass(), context)) {
                context.startService(serviceIntent);
            }
        }
    }
}