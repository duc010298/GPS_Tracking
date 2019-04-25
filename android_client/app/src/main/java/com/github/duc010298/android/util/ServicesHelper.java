package com.github.duc010298.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.android.services.DetectLocationChangeService;

import static android.content.Context.MODE_PRIVATE;

public class ServicesHelper {

    public void startDetectLocationChangeService(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences("ServicesStatus", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isDetectLocationRun", true);
        edit.apply();

        Intent broadcastIntent = new Intent("com.github.duc010298.android.RestartSensor");
        mContext.sendBroadcast(broadcastIntent);
    }

    public void stopDetectLocationChangeService(Context mContext, boolean isDetectLocationRun) {
        SharedPreferences pre2 = mContext.getSharedPreferences("ServicesStatus", MODE_PRIVATE);
        SharedPreferences.Editor edit2 = pre2.edit();
        edit2.putBoolean("isDetectLocationRun", isDetectLocationRun);
        edit2.apply();

        DetectLocationChangeService detectLocationChangeService = new DetectLocationChangeService();
        Intent serviceIntent = new Intent(mContext.getApplicationContext(), detectLocationChangeService.getClass());
        if (isMyServiceRunning(detectLocationChangeService.getClass(), mContext)) {
            mContext.stopService(serviceIntent);
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
