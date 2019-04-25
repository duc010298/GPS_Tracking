package com.github.duc010298.android;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.android.services.DetectLocationChangeService;

import static android.content.Context.MODE_PRIVATE;

public class SensorRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pre = context.getSharedPreferences("ServicesStatus", MODE_PRIVATE);
        if(pre.getBoolean("isDetectLocationRun", false)) {
            DetectLocationChangeService detectLocationChangeService = new DetectLocationChangeService();
            Intent serviceIntent = new Intent(context.getApplicationContext(), detectLocationChangeService.getClass());
            if (!isMyServiceRunning(detectLocationChangeService.getClass(), context)) {
                context.startService(serviceIntent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
