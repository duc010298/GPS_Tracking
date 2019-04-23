package com.github.duc010298.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.android.services.MyService;

import static android.content.Context.MODE_PRIVATE;

public class ServicesHelper {

    public void startMyService(Context mContext) {
        SharedPreferences pre = mContext.getSharedPreferences("ServicesStatus", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isLogout", false);
        edit.apply();

        MyService myService = new MyService();
        Intent myServiceIntent = new Intent(mContext.getApplicationContext(), myService.getClass());
        if (!isMyServiceRunning(myService.getClass(), mContext)) {
            mContext.startService(myServiceIntent);
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
