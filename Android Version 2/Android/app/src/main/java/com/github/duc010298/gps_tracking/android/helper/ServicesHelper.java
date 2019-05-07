package com.github.duc010298.gps_tracking.android.helper;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.duc010298.gps_tracking.android.services.ScheduleSendLocationHistory;
import com.github.duc010298.gps_tracking.android.services.TrackingLocationService;

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

    private void startTrackingLocationService(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isTrackingLocationRun", true);
        edit.apply();

        Intent broadcastIntent = new Intent("com.github.duc010298.gps_tracking.android.RestartTracking");
        context.sendBroadcast(broadcastIntent);
    }

    private void stopTrackingLocationService(Context context) {
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

    private void startJobServiceSendLocationHistory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);

        if(pre.getBoolean("isJobServiceSendLocationHistoryRun", true)) {
            SharedPreferences.Editor edit = pre.edit();
            edit.putBoolean("isJobServiceSendLocationHistoryRun", false);
            edit.apply();
            ComponentName componentName = new ComponentName(context, ScheduleSendLocationHistory.class);
            JobInfo.Builder builder = new JobInfo.Builder(100, componentName);

            builder
                    .setPeriodic(5400000)
                    .setPersisted(true);


            JobInfo jobInfo = builder.build();
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            jobScheduler.schedule(jobInfo);
        }
    }

    private void stopJobServiceSendLocationHistory(Context context) {
        SharedPreferences pre = context.getSharedPreferences("android_client", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putBoolean("isJobServiceSendLocationHistoryRun", true);
        edit.apply();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }

    public void startAllServices(Context context) {
        startTrackingLocationService(context);
        startJobServiceSendLocationHistory(context);
//        startWebSocketService(context);
    }

    public void stopAllServices(Context context) {
        stopTrackingLocationService(context);
        stopJobServiceSendLocationHistory(context);
//        stopWebSocketService(context);
    }
}
